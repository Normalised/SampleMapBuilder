package com.relivethefuture.dkb

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.regex.Pattern

class PathUtils {

    private static final Logger logger = LoggerFactory.getLogger(PathUtils.class);

    public static File getOutputDirectoryForLayout(File outputDirectory, File sourceDirectory, OutputLayout layout, String relativePath = null) {
        logger.debug("Get Output For Layout " + layout.getLayoutStyle().name)
        if(layout.getLayoutStyle() == LayoutStyle.ABSOLUTE) {
            // Using outputDir as base add on the number of directories from layout.outputStructureDepth
            return addDirectoryStructure(outputDirectory, sourceDirectory, layout.getOutputStructureDepth());
        } else if(layout.getLayoutStyle() == LayoutStyle.RELATIVE) {
            if(relativePath == null) {
                throw new Exception("Relative Path is null but layout is Relative")
            }
            // Using outputDir as base
            // 1. add on relativePath
            File extra = new File(outputDirectory, relativePath);
            // 2. add on the number of directories from layout.outputStructureDepth
            return addDirectoryStructure(extra, sourceDirectory, layout.getOutputStructureDepth());
        } else if(layout.getLayoutStyle() == LayoutStyle.SOURCE) {
            // Use instrument source directory as base
            return sourceDirectory;
        }
        return outputDirectory;
    }

    public static File addDirectoryStructure(File dest, File sourceDirectory, Integer depth) {
        if(depth == 0) {
            return dest
        }

        logger.debug("Add dir structure : " + dest.getAbsolutePath() + " : " + sourceDirectory.getAbsolutePath() + " : " + depth)

        String sourcePath = sourceDirectory.getAbsolutePath()
        Boolean isUnix = sourcePath.charAt(0) == '/'
        String p = Pattern.quote(isUnix ? "/" : "\\")
        String[] sourceParts = sourcePath.split(p)

        List<String> slist = sourceParts.toList()
        if(!isUnix) {
            slist.remove(0)
        }

        Integer lastElement = slist.size()

        String extraParts = "";
        if(lastElement >= depth) {
            int start = lastElement - depth;
            for(int i=0;i<depth;i++) {
                logger.debug("Adding part " + i + " : " + slist.get(start + i))
                extraParts += slist.get(start + i) + "/"
            }
        } else {
            extraParts = slist.join("/")
        }
        File out = new File(dest, extraParts);
        logger.debug("ADS : " + out.getAbsolutePath())
        return out;
    }
}
