package com.relivethefuture.kitbuilder.builder;

import java.util.EventObject;

public class BuildProgressEvent extends EventObject {

    private static final long serialVersionUID = 1L;

    private int total;
    private int current;

    public BuildProgressEvent(MapBuilder builder, int total, int current) {
        super(builder);
        this.total = total;
        this.current = current;
    }

}
