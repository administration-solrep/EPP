/*
 * (C) Copyright 2010 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Florent Guillaume
 */
package org.nuxeo.ecm.core.storage.sql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.Blobs;
import org.nuxeo.ecm.core.api.impl.blob.StringBlob;
import org.nuxeo.ecm.core.blob.binary.Binary;
import org.nuxeo.ecm.core.blob.binary.BinaryGarbageCollector;
import org.nuxeo.ecm.core.blob.binary.BinaryManagerStatus;
import org.nuxeo.ecm.core.blob.binary.LazyBinary;
import org.nuxeo.ecm.core.storage.sql.jdbc.JDBCConnection;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.ecm.core.test.StorageConfiguration;
import org.nuxeo.runtime.datasource.ConnectionHelper;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.transaction.TransactionHelper;

/*
 * Note that this unit test cannot be run with Nuxeo 5.4.0 (NXP-6021 needed).
 */
@RunWith(FeaturesRunner.class)
@Features(CoreFeature.class)
public class TestSQLBinaryManager {

    private static final String CONTENT = "this is a file au caf\u00e9";

    private static final String CONTENT_MD5 = "d25ea4f4642073b7f218024d397dbaef";

    private static final String CONTENT2 = "defg";

    private static final String CONTENT2_MD5 = "025e4da7edac35ede583f5e8d51aa7ec";

    public static final String TABLE = "binaries";

    @Inject
    protected CoreFeature coreFeature;

    protected SQLBinaryManager binaryManager;

    @Before
    public void setUp() throws Exception {
        StorageConfiguration database = coreFeature.getStorageConfiguration();
        assumeTrue(database.isVCS());

        // create table in database
        String dataSourceName = JDBCConnection.getDataSourceName(coreFeature.getRepositoryName());
        try (Connection connection = ConnectionHelper.getConnection(dataSourceName)) {
            try (Statement st = connection.createStatement()) {
                String blobType;
                String boolType;
                int size = 40; // max size for MD5 and SHA-256 hash
                if (database.isVCSH2()) {
                    blobType = "BLOB";
                    boolType = "BOOLEAN";
                } else if (database.isVCSPostgreSQL()) {
                    blobType = "BYTEA";
                    boolType = "BOOL";
                } else if (database.isVCSMySQL()) {
                    blobType = "BLOB";
                    boolType = "BIT";
                } else if (database.isVCSOracle()) {
                    blobType = "BLOB";
                    boolType = "NUMBER(1,0)";
                } else if (database.isVCSSQLServer()) {
                    blobType = "VARBINARY(MAX)";
                    boolType = "BIT";
                } else {
                    fail("Database " + database.getClass().getSimpleName() + " TODO");
                    return;
                }
                String sql = String.format("CREATE TABLE %s (%s VARCHAR(%d) PRIMARY KEY, %s %s, %s %s)", TABLE,
                        SQLBinaryManager.COL_ID, Integer.valueOf(size), SQLBinaryManager.COL_BIN, blobType,
                        SQLBinaryManager.COL_MARK, boolType);
                st.execute(sql);
            }
        }

        TransactionHelper.commitOrRollbackTransaction();
        TransactionHelper.startTransaction();

        // create binary manager
        binaryManager = new SQLBinaryManager();
        Map<String, String> properties = new HashMap<>();
        // ds name doesn't matter in single-connection mode
        properties.put(SQLBinaryManager.DS_PROP, "jdbc/NuxeoTestDS");
        properties.put(SQLBinaryManager.TABLE_PROP, TABLE);
        properties.put(SQLBinaryManager.CACHE_SIZE_PROP, "10MB");
        binaryManager.initialize("sql", properties);
    }

    @After
    public void tearDown() throws SQLException {
        if (binaryManager == null) {
            // not initialized, not VCS
            return;
        }
        String dataSourceName = JDBCConnection.getDataSourceName(coreFeature.getRepositoryName());
        try (Connection connection = ConnectionHelper.getConnection(dataSourceName)) {
            try (Statement st = connection.createStatement()) {
                String sql = String.format("DROP TABLE %s", TABLE);
                st.execute(sql);
            }
        }
    }

    @Test
    public void testSQLBinaryManager() throws Exception {
        // write to database
        Blob blob = new StringBlob(CONTENT, "application/octet-stream");
        Binary binary = binaryManager.getBinary(blob);
        try (InputStream is = binary.getStream()) {
            assertEquals(CONTENT, IOUtils.toString(is, "UTF-8"));
        }

        // read from database
        Binary binary2 = binaryManager.getBinary(binary.getDigest());
        assertNotNull(binary2);
        try (InputStream is = binary2.getStream()) {
            assertEquals(CONTENT, IOUtils.toString(is, "UTF-8"));
        }
    }

    @Test
    public void testSQLBinaryManagerDuplicate() throws Exception {
        Blob blob = new StringBlob(CONTENT, "application/octet-stream");
        binaryManager.getBinary(blob);

        // don't do collision checks to provoke insert collision
        SQLBinaryManager.disableCheckExisting = true;
        // and don't hit cache
        SQLBinaryManager.resetCache = true;

        // store again, shouldn't fail (collision detected)
        binaryManager.getBinary(blob);
    }

    @Test
    public void testSQLBinaryManagerGC() throws Exception {
        Binary binary = binaryManager.getBinary(CONTENT_MD5);
        assertTrue(binary instanceof LazyBinary);

        // store binary
        byte[] bytes = CONTENT.getBytes("UTF-8");
        binary = binaryManager.getBinary(Blobs.createBlob(CONTENT));
        assertNotNull(binary);

        // get binary
        binary = binaryManager.getBinary(CONTENT_MD5);
        assertNotNull(binary);
        assertEquals(CONTENT, IOUtils.toString(binary.getStream(), "UTF-8"));

        // another binary we'll keep
        binaryManager.getBinary(Blobs.createBlob(CONTENT2));

        // another binary we'll GC
        binaryManager.getBinary(Blobs.createBlob("abc"));

        // GC in non-delete mode
        BinaryGarbageCollector gc = binaryManager.getGarbageCollector();
        assertFalse(gc.isInProgress());
        gc.start();
        assertTrue(gc.isInProgress());
        gc.mark(CONTENT_MD5);
        gc.mark(CONTENT2_MD5);
        assertTrue(gc.isInProgress());
        gc.stop(false);
        assertFalse(gc.isInProgress());
        BinaryManagerStatus status = gc.getStatus();
        assertEquals(2, status.numBinaries);
        assertEquals(bytes.length + 4, status.sizeBinaries);
        assertEquals(1, status.numBinariesGC);
        assertEquals(3, status.sizeBinariesGC);

        // real GC
        gc = binaryManager.getGarbageCollector();
        gc.start();
        gc.mark(CONTENT_MD5);
        gc.mark(CONTENT2_MD5);
        gc.stop(true);
        status = gc.getStatus();
        assertEquals(2, status.numBinaries);
        assertEquals(bytes.length + 4, status.sizeBinaries);
        assertEquals(1, status.numBinariesGC);
        assertEquals(3, status.sizeBinariesGC);

        // another GC after not marking content 2
        gc = binaryManager.getGarbageCollector();
        gc.start();
        gc.mark(CONTENT_MD5);
        gc.stop(true);
        status = gc.getStatus();
        assertEquals(1, status.numBinaries);
        assertEquals(bytes.length, status.sizeBinaries);
        assertEquals(1, status.numBinariesGC);
        assertEquals(4, status.sizeBinariesGC);
    }

}
