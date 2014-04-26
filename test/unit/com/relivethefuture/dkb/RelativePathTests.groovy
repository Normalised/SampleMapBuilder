package com.relivethefuture.dkb

import com.relivethefuture.ResourceUtils
import griffon.test.GriffonUnitTestCase

/**
 * Created by martin on 05/06/13 at 11:49
 *
 */
class RelativePathTests extends GriffonUnitTestCase {

    void testSFZ() {
        File src = new File("D:\\tmp\\KitBuilder\\out\\absolute\\sfz\\in_Bassdrums.sfz");
        File dest = new File("D:\\tmp\\KitBuilder\\out\\absolute\\samples\\Bassdrums\\Bassdrum1.wav")
        String relative = ResourceUtils.getRelativePath(src, dest)
        assertEquals("..\\samples\\Bassdrums\\Bassdrum1.wav",relative)
    }

    void testRelativePaths() {
        File sampleFile = new File("D:\\tmp\\kitbuilder\\in\\samples\\sample.wav");
        File outputDir = new File("D:\\tmp\\kitbuilder\\out");

        String relative = ResourceUtils.getRelativePath(outputDir,sampleFile)
        assertEquals("..\\in\\samples\\sample.wav",relative)

        ArrayList<String> r1 = ResourceUtils.getRelativePathSections(outputDir,sampleFile);

        assertEquals(4, r1.size())
        assertEquals("..",r1.get(0))
        assertEquals("in",r1.get(1))
        assertEquals("samples",r1.get(2))
        assertEquals("sample.wav",r1.get(3))

        File a = new File("/a.wav");
        File b = new File("/b");
        ArrayList<String> a2b = ResourceUtils.getRelativePathSections(a,b);
        assertEquals(1,a2b.size());
        assertEquals("b",a2b.get(0))

        a = new File("/a/a.wav");
        b = new File("/b");
        ArrayList<String> aa2b = ResourceUtils.getRelativePathSections(a,b);
        assertEquals(2,aa2b.size());
        assertEquals("..",aa2b.get(0))
        assertEquals("b",aa2b.get(1))

    }
}
