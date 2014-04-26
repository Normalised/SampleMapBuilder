package com.relivethefuture.kitbuilder;

import com.relivethefuture.ResourceUtils;
import com.relivethefuture.dkb.OutputLayout;
import com.relivethefuture.formats.SamplerFormat;
import com.relivethefuture.kitbuilder.model.FolderOfSamples;
import com.relivethefuture.kitbuilder.output.OutputConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KitFileNameGenerator {
    private final Logger logger = LoggerFactory.getLogger(KitFileNameGenerator.class);

    private OutputLayout layout;
    private OutputConfig config;

    public String generateFileName(FolderOfSamples fos, String extra) {

        String mapFileName = "";
        if(extra == null) {
            extra = "";
        }

        SamplerFormat format = config.getFormat();

        logger.debug("Generate filename for " + fos.getSourceDirectory().getName() + " : " + config.getFormat() + " : " + extra);
        if(fos.getName() != null) {
            List<String> pathSections = ResourceUtils.getPathSections(fos.getSourceDirectory());
            // If there are more sections in the path than required just remove them
            logger.debug("PS " + pathSections.size() + " : " + layout.getFilenameDepth());
            if(pathSections.size() > layout.getFilenameDepth()) {
                int spare = pathSections.size() - layout.getFilenameDepth();
                for(int i=0;i<spare;i++) {
                    pathSections.remove(0);
                }
            }
            if(pathSections.size() == 1) {
                mapFileName = pathSections.get(0);
            } else {
                mapFileName = pathSections.join("_");
            }

        } else {
            logger.warn("FOS name is null");
            mapFileName = "Unknown";
        }

        mapFileName += extra + "." + format.extension();
        return mapFileName;
    }

    public OutputLayout getLayout() {
        return layout;
    }

    public void setLayout(OutputLayout layout) {
        this.layout = layout;
    }

    public OutputConfig getConfig() {
        return config;
    }

    public void setConfig(OutputConfig config) {
        this.config = config;
    }
}
