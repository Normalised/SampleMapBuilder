package com.relivethefuture.dkb

import com.relivethefuture.FilenameUtils
import org.junit.Before
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static org.junit.Assert.assertEquals

/**
 * Created by martin on 20/04/13 at 16:54
 *
 */
class PathUtilsTest {

    private final Logger logger = LoggerFactory.getLogger(PathUtilsTest.class);

    @Before
    public void setUp() throws Exception {


    }

    void testAddStructure() {
        File dest = new File("D:/a")
        File source = new File("D:/b/c/d/e/f")

        File a1 = PathUtils.addDirectoryStructure(dest, source, 1)
        assertEquals("D:\\a\\f",a1.getAbsolutePath())
        File a2 = PathUtils.addDirectoryStructure(dest, source, 2)
        assertEquals("D:\\a\\e\\f",a2.getAbsolutePath())
        File a3 = PathUtils.addDirectoryStructure(dest, source, 3)
        assertEquals("D:\\a\\d\\e\\f",a3.getAbsolutePath())
        File all = PathUtils.addDirectoryStructure(dest, source, 10)
        assertEquals("D:\\a\\b\\c\\d\\e\\f",all.getAbsolutePath())
    }

    void testAbsoluteOutput() {
        File outputDirectory = new File("D:/tmp")
        File sourceDirectory = new File("D:/audio/samples")
        String relativePath = null
        OutputLayout layout = new OutputLayout()

        layout.layoutStyle = LayoutStyle.ABSOLUTE
        layout.outputStructureDepth = 0
        File out = PathUtils.getOutputDirectoryForLayout(outputDirectory, sourceDirectory, layout, relativePath)
        assertEquals out, outputDirectory
    }

    void testRelativeOutput() {
        File outputDirectory = new File("D:/tmp")
        File sourceDirectory = new File("D:/audio/samples")
        String relativePath = "relative"
        OutputLayout layout = new OutputLayout()

        layout.layoutStyle = LayoutStyle.RELATIVE
        layout.outputStructureDepth = 0
        File out = PathUtils.getOutputDirectoryForLayout(outputDirectory, sourceDirectory, layout, relativePath)
        assertEquals out, new File(outputDirectory, relativePath)
    }
}
