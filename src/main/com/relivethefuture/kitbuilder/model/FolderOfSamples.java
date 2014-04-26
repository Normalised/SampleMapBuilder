package com.relivethefuture.kitbuilder.model;

import com.relivethefuture.generic.BasicSample;
import com.relivethefuture.generic.Sample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Represents a directory of sampleLists to be written as a map file
 * Also creates a set of zones for the samples.
 */
public class FolderOfSamples implements Comparator<FolderOfSamples>, Comparable<FolderOfSamples> {

    private final Logger log = LoggerFactory.getLogger(FolderOfSamples.class);

    // Lists of sample file lists, each sublist contains a maximum of 127 sampleLists.
    public ArrayList<Sample> samples;
    // Directory where the actual samples are
    private File directory;

    // Where the original samples are
    private File sourceDirectory;
    private String name;

    private Comparator comparator;

    public FolderOfSamples(File directory, ArrayList<Sample> audioFiles) {
        this.sourceDirectory = directory;
        this.samples = audioFiles;
        name = directory.getName();
    }

    public File getSourceDirectory() {
        return sourceDirectory;
    }

    public void setSourceDirectory(File sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }

    public void setDirectory(File dir) {
        directory = dir;
    }

    public File getDirectory() {
        return directory;
    }

    public String getName() {
        return name;
    }

    public void setName(String n) {
        this.name = n;
    }

    public int compare(FolderOfSamples folderOfSamples, FolderOfSamples folderOfSamples2) {
        return folderOfSamples.samples.size() - folderOfSamples2.samples.size();
    }

    public int compareTo(FolderOfSamples folderOfSamples) {
        return samples.size() - folderOfSamples.samples.size();
    }

    public FolderOfSamples (FolderOfSamples source) {
        samples = new ArrayList<Sample>();
        Sample newSample = null;
        for(Sample s : source.samples) {
            newSample = new BasicSample(s);
            samples.add(newSample);
        }

        log.debug("Copied samples to FOS");
        directory = source.getDirectory();
        log.debug("Set directory to " + directory);
        name = source.name;
    }

    public Comparator getSampleComparator() {
        return comparator;
    }

    public void setSampleComparator(Comparator sorter) {
        this.comparator = sorter;
    }

}
