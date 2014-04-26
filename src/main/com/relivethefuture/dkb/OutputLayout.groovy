package com.relivethefuture.dkb

class OutputLayout {

    private LayoutStyle layoutStyle

    public static Integer ALL_DIRS = 10

    // How many levels of directory structure to replicate in the output
    // Minimum 0
    // Maximum ALL
    Integer outputStructureDepth = 0
    // How many levels of directory structure to use in the name of each map file
    // Minimum 1
    // Maximum ALL
    Integer filenameDepth = 1

    public OutputLayout() {
        layoutStyle = LayoutStyle.ABSOLUTE;
    }

    Integer getOutputStructureDepth() {
        return outputStructureDepth
    }

    void setOutputStructureDepth(Integer outputStructureDepth) {

        if(outputStructureDepth < 0) {
            outputStructureDepth = 0
        }
        if(outputStructureDepth > ALL_DIRS) {
            outputStructureDepth = ALL_DIRS
        }

        this.outputStructureDepth = outputStructureDepth
    }

    Integer getFilenameDepth() {
        return filenameDepth
    }

    void setFilenameDepth(Integer filenameDepth) {
        if(filenameDepth < 1) {
            filenameDepth = 1
        } else if(filenameDepth > ALL_DIRS) {
            filenameDepth = ALL_DIRS
        }

        this.filenameDepth = filenameDepth
    }

    public void setLayoutStyle(LayoutStyle style) {
        layoutStyle = style;
    }

    public LayoutStyle getLayoutStyle() {
        return layoutStyle;
    }
}
