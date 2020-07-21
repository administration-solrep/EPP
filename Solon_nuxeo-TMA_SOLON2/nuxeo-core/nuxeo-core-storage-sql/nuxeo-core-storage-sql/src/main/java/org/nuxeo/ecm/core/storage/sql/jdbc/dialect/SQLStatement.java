/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Florent Guillaume
 */
package org.nuxeo.ecm.core.storage.sql.jdbc.dialect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nuxeo.ecm.core.storage.sql.Activator;
import org.nuxeo.ecm.core.storage.sql.jdbc.JDBCConnection;

/**
 * A SQL statement and some optional tags that condition execution.
 */
public class SQLStatement {

    // for derby...
    public static final String DIALECT_WITH_NO_SEMICOLON = "noSemicolon";

    /** Category pseudo-tag */
    public static final String CATEGORY = "#CATEGORY:";

    /**
     * Tags that may condition execution of the statement.
     */
    public static class Tag {

        /**
         * Tag for a SELECT statement whose number of rows must be counted. Var
         * "emptyResult" is set accordingly.
         */
        public static final String TAG_TEST = "#TEST:";

        /**
         * Tag to only execute statement if a var is true. Var may be preceded
         * by ! inverse the test.
         */
        public static final String TAG_IF = "#IF:";

        public static final String VAR_EMPTY_RESULT = "emptyResult";

        /** Tag: TAG_TEST, TAG_IF */
        public final String key;

        /** The value behind a tag, used for TAG_IF */
        public final String value;

        public Tag(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    /** SQL statement */
    public final String sql;

    /** Tags on the statement */
    public final List<Tag> tags;

    public SQLStatement(String sql, List<Tag> tags) {
        this.sql = sql;
        this.tags = tags == null ? Collections.<Tag> emptyList() : tags;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("SQLStatement(");
        for (Tag tag : tags) {
            buf.append(tag.key);
            String value = tag.value;
            if (value != null) {
                buf.append(' ');
                buf.append(value);
            }
            buf.append(", ");
        }
        buf.append(sql);
        buf.append(')');
        return buf.toString();
    }

    /**
     * Reads SQL statements from a text file.
     * <p>
     * Statements have a category, and optional tags (that may condition
     * execution).
     *
     * <pre>
     *   #CATEGORY: mycat
     *   #TEST:
     *   SELECT foo
     *     from bar;
     * </pre>
     *
     * <pre>
     *   #CATEGORY: mycat
     *   #IF: emptyResult
     *   #IF: somethingEnabled
     *   INSERT INTO ...;
     * </pre>
     *
     * An empty line terminates a statement.
     */
    public static Map<String, List<SQLStatement>> read(String filename,
            Map<String, List<SQLStatement>> statements) throws IOException {
        InputStream is = Activator.getResourceAsStream(filename);
        if (is == null) {
            throw new IOException("Cannot open: " + filename);
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(is,
                "UTF-8"));
        String line;
        String category = null;
        List<Tag> tags = null;
        try {
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(SQLStatement.CATEGORY)) {
                    category = line.substring(SQLStatement.CATEGORY.length()).trim();
                    continue;
                } else if (line.startsWith(Tag.TAG_TEST)
                        || line.startsWith(Tag.TAG_IF)) {
                    String key = line.substring(0, line.indexOf(':') + 1);
                    String value = line.substring(key.length()).trim();
                    if (value.length() == 0) {
                        value = null;
                    }
                    if (tags == null) {
                        tags = new LinkedList<Tag>();
                    }
                    tags.add(new Tag(key, value));
                    continue;
                } else if (line.startsWith("#")) {
                    continue;
                }
                StringBuilder buf = new StringBuilder();
                boolean read = false;
                while (true) {
                    if (read) {
                        line = reader.readLine();
                    } else {
                        read = true;
                    }
                    if (line == null || line.trim().equals("")) {
                        if (buf.length() == 0) {
                            break;
                        }
                        String sql = buf.toString().trim();
                        SQLStatement statement = new SQLStatement(sql, tags);
                        List<SQLStatement> catStatements = statements.get(category);
                        if (catStatements == null) {
                            statements.put(category,
                                    catStatements = new LinkedList<SQLStatement>());
                        }
                        catStatements.add(statement);
                        break;
                    } else if (line.startsWith("#")) {
                        continue;
                    } else {
                        buf.append(line);
                        buf.append('\n');
                    }
                }
                tags = null;
                if (line == null) {
                    break;
                }
            }
        } finally {
            reader.close();
        }
        return statements;
    }

    protected static String replaceVars(String sql,
            Map<String, Serializable> properties) {
        if (properties != null) {
            for (Entry<String, Serializable> en : properties.entrySet()) {
                String key = "${" + en.getKey() + "}";
                String value = String.valueOf(en.getValue());
                sql = sql.replaceAll(Pattern.quote(key),
                        Matcher.quoteReplacement(value));
            }
        }
        return sql;
    }

    /**
     * Executes a list of SQL statements, following the tags.
     */
    public static void execute(List<SQLStatement> statements,
            Map<String, Serializable> properties, JDBCConnection jdbc)
            throws SQLException {
        Statement st = jdbc.connection.createStatement();
        try {
            STATEMENT: //
            for (SQLStatement statement : statements) {
                boolean test = false;
                for (Tag tag : statement.tags) {
                    if (tag.key.equals(Tag.TAG_TEST)) {
                        test = true;
                    } else if (tag.key.equals(Tag.TAG_IF)) {
                        String key = tag.value;
                        boolean neg = key.startsWith("!");
                        if (neg) {
                            key = key.substring(1).trim();
                        }
                        Serializable value = properties.get(key);
                        if (value == null) {
                            jdbc.logger.error("Unknown condition: " + key);
                            continue STATEMENT;
                        }
                        if (!(value instanceof Boolean)) {
                            jdbc.logger.error("Not a boolean condition: " + key);
                            continue STATEMENT;
                        }
                        if (((Boolean) value).booleanValue() == neg) {
                            // condition failed
                            continue STATEMENT;
                        }
                        // ok
                    }
                }
                String sql = statement.sql;
                sql = replaceVars(sql, properties);
                if (sql.startsWith("LOG.DEBUG")) {
                    String msg = sql.substring("LOG.DEBUG".length()).trim();
                    jdbc.logger.log(msg);
                    continue;
                } else if (sql.startsWith("LOG.INFO")) {
                    String msg = sql.substring("LOG.INFO".length()).trim();
                    jdbc.logger.info(msg);
                    continue;
                } else if (sql.startsWith("LOG.ERROR")) {
                    String msg = sql.substring("LOG.ERROR".length()).trim();
                    jdbc.logger.error(msg);
                    continue;
                } else if (sql.startsWith("LOG.FATAL")) {
                    String msg = sql.substring("LOG.FATAL".length()).trim();
                    jdbc.logger.error(msg);
                    throw new SQLException("Fatal error: " + msg);
                }

                jdbc.logger.log(sql.replace("\n", "\n    ")); // indented
                if (sql.endsWith(";")
                        && properties.containsKey(DIALECT_WITH_NO_SEMICOLON)) {
                    // derby at least doesn't allow a terminating semicolon
                    sql = sql.substring(0, sql.length() - 1);
                }

                try {
                    if (test) {
                        ResultSet rs = st.executeQuery(sql);
                        Boolean emptyResult = Boolean.valueOf(!rs.next());
                        properties.put(Tag.VAR_EMPTY_RESULT, emptyResult);
                        jdbc.logger.log("  -> emptyResult = " + emptyResult);
                    } else {
                        st.execute(sql);
                    }
                } catch (SQLException e) {
                    throw new SQLException("Error executing: " + sql + " : "
                            + e.getMessage(), e);
                }
            }
        } finally {
            st.close();
        }
    }

}
