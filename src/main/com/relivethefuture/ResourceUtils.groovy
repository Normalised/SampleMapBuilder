package com.relivethefuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

public class ResourceUtils {

    private static final Logger logger = LoggerFactory.getLogger(ResourceUtils.class);

    /**
     * Get a list of the sections of a path from the filesystem root
     * e.g.
     * D:/a/b/c -> [a,b,c]
     * /home/martin/a/b/c -> [home,martin,a,b,c]
     *
     *
     * @param file
     * @return
     */
    public static List<String> getPathSections(File file) {

        String absolutePath = file.getAbsolutePath();
        Boolean isUnix = absolutePath.charAt(0) == '/';

        String[] split = absolutePath.split(isUnix ? "/" : Pattern.quote("\\"));
        List<String> sections = new ArrayList<String>(Arrays.asList(split));
        if(!isUnix) {
            sections.remove(0);
        }
        return sections;
    }

    public static ArrayList<String> getRelativePathSections(File from, File to) {

        logger.debug("Get relative path sections " + from + " : " + to);
        ArrayList<String> parts = new ArrayList<String>();

        String destPath = to.getAbsolutePath();
        String sourcePath = from.getAbsolutePath();

        Boolean isUnix = destPath.charAt(0) == '/';

        if(destPath.equals(sourcePath)) {
            return parts;
        }

        // Normalize the paths
        String normalizedDestPath = FilenameUtils.normalizeNoEndSeparator(destPath);
        String normalizedSourcePath = FilenameUtils.normalizeNoEndSeparator(sourcePath);

        String pathSeparator = "\\";
        if(!isUnix) {
            normalizedDestPath = FilenameUtils.separatorsToWindows(normalizedDestPath);
            normalizedSourcePath = FilenameUtils.separatorsToWindows(normalizedSourcePath);
        } else {
            normalizedDestPath = FilenameUtils.separatorsToUnix(normalizedDestPath);
            normalizedSourcePath = FilenameUtils.separatorsToUnix(normalizedSourcePath);
            pathSeparator = "/";
        }

        logger.debug("NTP " + normalizedDestPath);
        logger.debug("NBP " + normalizedSourcePath);

        String quotedSeparator = Pattern.quote(pathSeparator);
        String[] sourceParts = normalizedSourcePath.split(quotedSeparator);
        String[] destParts = normalizedDestPath.split(quotedSeparator);

        //logger.debug("BL " + base.length + ". TL : " + target.length);
        // First get all the common elements. Store them as a string,
        // and also count how many of them there are.
        StringBuffer common = new StringBuffer();

        int commonIndex = 0;
        while (commonIndex < destParts.length && commonIndex < sourceParts.length && destParts[commonIndex].equals(sourceParts[commonIndex])) {
            common.append(destParts[commonIndex] + pathSeparator);
            commonIndex++;
        }

        if (commonIndex == 0) {
            // No single common path element. This most
            // likely indicates differing drive letters, like C: and D:.
            // These paths cannot be relativized.
            throw new PathResolutionException("No common path element found for '" + normalizedDestPath + "' and '" + normalizedSourcePath
                    + "'");
        }

        // The number of directories we have to backtrack depends on whether the base is a file or a dir
        // For example, the relative path from
        //
        // /foo/bar/baz/gg/ff to /foo/bar/baz
        //
        // ".." if ff is a file
        // "../.." if ff is a directory
        //
        // The following is a heuristic to figure out if the base refers to a file or dir. It's not perfect, because
        // the resource referred to by this path may not actually exist, but it's the best I can do
        boolean sourceIsFile = true;

        if (from.exists()) {
            sourceIsFile = from.isFile();

        } else if (sourcePath.endsWith(pathSeparator)) {
            sourceIsFile = false;
        }

        StringBuffer relative = new StringBuffer();

        if (sourceParts.length != commonIndex) {
            int numDirsUp = sourceIsFile ? sourceParts.length - commonIndex - 1 : sourceParts.length - commonIndex;

            for (int i = 0; i < numDirsUp; i++) {
                parts.add("..");
            }
        }
        if(common.length() >= normalizedDestPath.length()) {
            return parts;
        }
        String remaining = normalizedDestPath.substring(common.length());
        String[] remainingParts = remaining.split(quotedSeparator);
        parts.addAll(Arrays.asList(remainingParts));
        return parts;
    }
    /**
     * Get the relative path from one file to another, specifying the directory separator. 
     * If one of the provided resources does not exist, it is assumed to be a file unless it ends with '/' or
     * '\'.
     * 
     * @param target targetPath is calculated to this file
     * @param base basePath is calculated from this file
     */
    public static String getRelativePath(File target, File base) {

        logger.debug("Get relative path : " + target + " : " + base);
        ArrayList<String> sections = getRelativePathSections(target, base);
        Boolean isUnix = target.getAbsolutePath().charAt(0) == '/';
        if(sections.size() == 0) {
            return "";
        } else if(sections.size() == 1) {
            return sections.get(0);
        } else {
            if(isUnix) {
                return sections.join("/");
            } else {
                return sections.join("\\");
            }
        }
    }


    static class PathResolutionException extends RuntimeException {
        PathResolutionException(String msg) {
            super(msg);
        }
    }    
}