package com.relivethefuture.dkb

import com.relivethefuture.formats.SamplerFormat
import com.relivethefuture.kitbuilder.KitFileNameGenerator
import com.relivethefuture.kitbuilder.model.FolderOfSamples
import com.relivethefuture.kitbuilder.output.OutputConfig
import griffon.test.GriffonUnitTestCase

/**
 * Created by martin on 06/06/13 at 09:25
 *
 */
class FilenameGeneratorTest extends GriffonUnitTestCase {

    void testFilenameGenerator() {
        KitFileNameGenerator kitFileNameGenerator = new KitFileNameGenerator()
        OutputConfig testConfig = new OutputConfig()
        testConfig.format = SamplerFormat.SFZ
        OutputLayout outputLayout = new OutputLayout()

        kitFileNameGenerator.config = testConfig
        kitFileNameGenerator.layout = outputLayout

        FolderOfSamples fos = new FolderOfSamples(new File("/a/b/c"),null)
        String filename

        outputLayout.filenameDepth = 1
        filename = kitFileNameGenerator.generateFileName(fos,"")
        assertEquals("c.sfz",filename)

        outputLayout.filenameDepth = 2
        filename = kitFileNameGenerator.generateFileName(fos,"")
        assertEquals("b_c.sfz",filename)

        outputLayout.filenameDepth = 3
        filename = kitFileNameGenerator.generateFileName(fos,"")
        assertEquals("a_b_c.sfz",filename)

        outputLayout.filenameDepth = 10
        filename = kitFileNameGenerator.generateFileName(fos,"")
        assertEquals("a_b_c.sfz",filename)
    }
}
