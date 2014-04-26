package com.relivethefuture;

import com.relivethefuture.generic.BasicSample;
import com.relivethefuture.generic.Sample;
import com.relivethefuture.kitbuilder.model.FolderOfSamples;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FileSystemScanner {

    private final Logger logger = LoggerFactory.getLogger(FileSystemScanner.class);

    private List<FolderOfSamples> items;

    private boolean skipEmptyDirectories = true;
    private boolean recursive = true;

    private Integer totalNumberOfSamples;

    private Queue<File> directoryQueue;
    private File rootDirectory;

    public FileSystemScanner() {
        reset();
    }

    public void reset() {
        totalNumberOfSamples = 0;
        if(items == null) {
            items = new ArrayList<FolderOfSamples>();
        } else {
            items.clear();
        }
        if(directoryQueue == null) {
            directoryQueue = new ConcurrentLinkedQueue<File>();
        } else {
            directoryQueue.clear();
        }
    }

    public Integer getTotalNumberOfSamples() {
        return totalNumberOfSamples;
    }

    public void setRecursive(boolean r) {
        recursive = r;
    }

    public void setRoot(File root) throws Exception {
        rootDirectory = root;
        try {
            Boolean isDirectory = root.isDirectory();
            if(!isDirectory && root.isFile()) {
                logger.debug("Root isnt a directory, but its a file, using parent : " + root.getParent());
                rootDirectory = root.getParentFile();
            }
        } catch(SecurityException e) {
            logger.error("Security Exception checking isDirectory for " + root.getAbsolutePath(),e);
        }

        if(rootDirectory != null) {
            directoryQueue.clear();
            directoryQueue.add(rootDirectory);
        } else {
            throw new Exception("Root directory is null : " + root.getAbsolutePath());
        }
    }

    public File work() {
        File dir = directoryQueue.poll();
        if(dir != null) {
            //logger.debug("Polled " + dir.getName());
            scan(dir);
        }
        return dir;
    }
    /**
     * Scan the provided path and create a {@link FolderOfSamples} for each directory
     * A recursive scan will populate the directoryQueue with a list of directories waiting to be scanned
     */
    public ArrayList<File> scan(File dir) {
        File[] entries = dir.listFiles();
        ArrayList<File> directories = new ArrayList<File>();
        ArrayList<Sample> audioFiles = new ArrayList<Sample>();

        logger.debug("Scanning " + dir.getAbsolutePath() + " with " + entries.length + " items");

        String name = null;
        String lcName = null;

        for (File f : entries) {
            name = f.getName();
            if (f.isDirectory() && name.length() > 2) {
                directories.add(f);
            } else {
                lcName = name.toLowerCase();
                if (lcName.endsWith(".wav") || lcName.endsWith(".aif") || lcName.endsWith(".aiff")) {
                    audioFiles.add(new BasicSample(f));
                }
            }
        }

        if (audioFiles.size() > 0) {
            addSampleFilesDir(dir, audioFiles);
        }

        if(recursive) {
            for (File directory : directories) {
                directoryQueue.add(directory);
            }
            return null;
        } else {
            return directories;
        }
    }

    private void addSampleFilesDir(File dir, ArrayList<Sample> audioFiles) {
        FolderOfSamples item = new FolderOfSamples(dir, audioFiles);
        if(skipEmptyDirectories && item.samples.size() == 0) {
            return;
        }
        items.add(item);
        totalNumberOfSamples += item.samples.size();
    }

    public List<FolderOfSamples> getItems() {
        return items;
    }

    public boolean skipEmptyDirectories() {
        return skipEmptyDirectories;
    }

    public void setSkipEmptyDirectories(boolean skipEmptyDirectories) {
        this.skipEmptyDirectories = skipEmptyDirectories;
    }

    public File getRootDirectory() {
        return rootDirectory;
    }
}
