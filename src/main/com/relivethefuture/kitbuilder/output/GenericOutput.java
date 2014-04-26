package com.relivethefuture.kitbuilder.output;

import java.io.File;

/**
 * Created by martin on 15/04/13 at 18:03
 */
public class GenericOutput {
    private File outputDir;
    private Boolean active = true;
    private Integer rootNote;

    private String relativePath;

    public File getOutputDirectory() {
        return outputDir;
    }

    public void setOutputDirectory(File outputDir) {
        this.outputDir = outputDir;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public boolean useFixedRoot() {
        return rootNote != null;
    }

    public Integer getRoot() {
        return rootNote;
    }

    public void setRoot(Integer root) {
        rootNote = root;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }
}
