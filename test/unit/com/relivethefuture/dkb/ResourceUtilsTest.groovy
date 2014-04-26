package com.relivethefuture.dkb

import com.relivethefuture.ResourceUtils
import griffon.test.GriffonUnitTestCase

/**
 * Created by martin on 06/06/13 at 09:40
 *
 */
class ResourceUtilsTest extends GriffonUnitTestCase {

    void testPathSections() {
        File f = new File("/a/b/c")
        List<String> fp = ResourceUtils.getPathSections(f)

        assertEquals 3, fp.size()
        assertEquals "a", fp.get(0)
        assertEquals "b", fp.get(1)
        assertEquals "c", fp.get(2)

        File f2 = new File("D:\\a\\b\\c")
        List<String> fp2 = ResourceUtils.getPathSections(f)

        assertEquals 3, fp2.size()
        assertEquals "a", fp2.get(0)
        assertEquals "b", fp2.get(1)
        assertEquals "c", fp2.get(2)

    }
}
