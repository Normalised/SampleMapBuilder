package com.relivethefuture.kitbuilder.writers;

import com.relivethefuture.kitbuilder.SimpleProgressListener;
import net.sf.jtpl.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by martin on 20/01/12 at 17:01
 */
public abstract class BasicInstrumentWriter implements InstrumentWriter {

    private final Logger logger = LoggerFactory.getLogger(BasicInstrumentWriter.class);

    private ArrayList<SimpleProgressListener> listeners;
    protected ClassLoader classLoader;

    public BasicInstrumentWriter() {
        classLoader = Thread.currentThread().getContextClassLoader();
    }

    public void addProgressListener(SimpleProgressListener listener) {
        if(listeners == null) {
            listeners = new ArrayList<SimpleProgressListener>();
        }
        listeners.add(listener);
    }

    public void removeProgressListener(SimpleProgressListener listener) {
        if(listeners != null) {
            listeners.remove(listener);
        }
    }
    
    protected void reportProgress(Float percent) {
        for (SimpleProgressListener listener : listeners) {
            listener.reportProgress(percent);
        }
    }

    protected String[] getFilenameAndExtension(File f) {
        String[] parts = new String[2];

        int dotPos = f.getName().lastIndexOf(".");
        parts[0] = f.getName().substring(0, dotPos);
        parts[1] = f.getName().substring(dotPos + 1);
        return parts;
    }

    protected Template getTemplate(String name) throws IOException {
        logger.debug("Get Template " + name);
        InputStream inputStream = classLoader.getResourceAsStream("templates/" + name);
        InputStreamReader reader = new InputStreamReader(inputStream);
        return new Template(reader);
    }
}
