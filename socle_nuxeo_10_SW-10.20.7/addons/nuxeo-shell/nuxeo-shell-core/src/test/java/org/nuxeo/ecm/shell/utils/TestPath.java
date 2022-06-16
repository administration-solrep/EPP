/*
 * (C) Copyright 2014 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     Thierry Martins
 */
package org.nuxeo.ecm.shell.utils;

import org.junit.Test;
import org.nuxeo.shell.utils.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TestPath {

    @Test
    public void testPathInit() {
        Path path = new Path("/");
        assertEquals(path.lastSegment(), null);

        path = new Path("/foo");
        assertEquals(path.lastSegment(), "foo");

        path = path.append(new Path(".bar"));
        assertEquals(path.lastSegment(), ".bar");

        path = new Path("/.foo");
        assertEquals(path.lastSegment(), ".foo");
        assertEquals(1, path.segmentCount());

        path = new Path("/foo/bar");
        assertEquals(path.lastSegment(), "bar");
        assertEquals(2, path.segmentCount());

        path = new Path("/foo/.bar");
        assertEquals(path.lastSegment(), ".bar");
        assertEquals(2, path.segmentCount());

        path = new Path("/./.foo");
        assertEquals(path.lastSegment(), ".foo");
        assertEquals(1, path.segmentCount());

        path = new Path("/../.foo");
        assertEquals(path.lastSegment(), ".foo");
        assertEquals(2, path.segmentCount());

        path = new Path("//.foo");
        assertEquals(path.lastSegment(), ".foo");
        assertEquals(1, path.segmentCount());

        path = new Path("//foo/.//../.bar");
        assertEquals(path.lastSegment(), ".bar");
        assertEquals(1, path.segmentCount()); // XXX: hard to explain?
    }

    @Test
    public void testRemoveSegments() {
        Path path = new Path("/");
        assertEquals(path.removeFirstSegments(1), new Path(""));

        path = new Path("/foo");
        assertEquals(path.removeFirstSegments(1), new Path(""));
        assertEquals(path.removeFirstSegments(2), new Path(""));

        path = new Path("/foo/bar");
        assertEquals(path.removeFirstSegments(1), new Path("bar")); // should be /bar
        assertEquals(path.removeFirstSegments(2), new Path(""));

        path = new Path("//foo/.//../.bar");
        assertEquals(path.lastSegment(), ".bar");
        assertEquals(path.removeFirstSegments(1), new Path("")); // why only one segment?

    }

    /*
     * Below are tests from nuxeo-common:Path to check we have almost a similar behavior
     */

    @Test
    public void test() {
        Path path = new Path("/a/b/c/d");

        assertFalse(path.isRoot());
        assertFalse(path.isEmpty());

        assertTrue(path.isAbsolute());

        assertEquals("/a/b/c/d", path.toString());
        assertEquals(4, path.segmentCount());
        assertEquals(4, path.segments().length);
        assertEquals("a", path.segment(0));
        assertEquals("b", path.segment(1));
        assertEquals("c", path.segment(2));
        assertEquals("d", path.segment(3));
        assertNull(path.segment(4));
        assertEquals("d", path.lastSegment());

        assertEquals(new Path("b/c/d"), path.removeFirstSegments(1));
        assertEquals(new Path("/a/b/c"), path.removeLastSegments(1));
    }

    @Test
    public void testEquals() {
        Path path = new Path("/a/b/c/d");
        Path path2 = new Path("/a/b/c/d/");
        Path path3 = new Path("/a/b/c////d");
        Path path4 = new Path("/a/b/././c/./e/../d");
        Path path5 = new Path("/./a/b/c/d");
        Path path6 = new Path("/a//b/c/d");

        assertEquals(path, path);
        // assertEquals(path, path2); // This behavior is different from nuxeo-common:Path
        assertEquals(path, path3);
        assertEquals(path, path4);
        assertEquals(path, path5);
        assertEquals(path, path6);

        assertEquals(path.hashCode(), path2.hashCode());
        assertEquals(path.hashCode(), path3.hashCode());
        assertEquals(path.hashCode(), path4.hashCode());
        assertEquals(path.hashCode(), path5.hashCode());

        assertFalse(path.equals(null));

        assertFalse(path.equals(new Path("a/b/c/d")));
        assertFalse(path.equals(new Path("/a/b/c/e")));
        assertFalse(path.equals(new Path("/a/b/c/d/e")));
    }

    @Test
    public void testGetFileExtension() {
        assertNull(new Path("/a/b/c/").getFileExtension());
        assertNull(new Path("/a/b/c").getFileExtension());
        assertEquals("doc", new Path("/a/b/c.doc").getFileExtension());
    }

    @Test
    public void testBasic() {
        final Path path = new Path("/a/b/c");

        assertEquals(3, path.segmentCount());
        assertTrue(path.isAbsolute());
        assertFalse(path.isEmpty());
        assertFalse(path.isRoot());
        assertEquals(3, path.segments().length);
        assertEquals("a", path.segment(0));
        assertEquals("b", path.segment(1));
        assertEquals("c", path.segment(2));

    }

    @Test
    public void testFileExtension() {
        Path path = new Path("/a/b/c");
        assertNull(path.getFileExtension());

        path = new Path("/a/b/c.txt");
        assertEquals("txt", path.getFileExtension());
        assertEquals("/a/b/c", path.removeFileExtension().toString());

        path = new Path("/a/b/c/");
        assertNull(path.getFileExtension());
    }

    @Test
    public void testPathNormalisation() {
        Path path = new Path("////a/./b/../c");
        assertEquals("/a/c", path.toString());
    }

    @Test
    public void testEquality() {
        assertEquals(new Path("/a/b/c"), new Path("/a/b/c"));
        assert !new Path("/a/b/c").equals(new Path("/a/b"));
    }

    @Test
    public void testAppend() {
        Path path1 = new Path("/a/b/c");
        Path path2 = new Path("/d/e/f");
        Path path3 = new Path("/a/b/c/d/e/f");
        assertEquals(path1.append(path2), path3);
    }

}
