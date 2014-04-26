package com.relivethefuture.kitbuilder.builder;

import java.util.EventListener;

public interface BuildProgressListener extends EventListener {
    public void handleProgressEvent(BuildProgressEvent event);
}
