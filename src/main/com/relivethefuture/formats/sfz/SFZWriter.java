package com.relivethefuture.formats.sfz;

import com.relivethefuture.ResourceUtils;
import com.relivethefuture.generic.Instrument;
import com.relivethefuture.generic.MidiRange;
import com.relivethefuture.generic.Zone;
import com.relivethefuture.kitbuilder.writers.BasicInstrumentWriter;
import com.relivethefuture.kitbuilder.output.OutputConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class SFZWriter extends BasicInstrumentWriter {

    private final Logger logger = LoggerFactory.getLogger(SFZWriter.class);

    private OutputConfig outputConfig;

    private File instrumentFile;

    public SFZWriter(OutputConfig sfzConfig) {
        super();
        outputConfig = sfzConfig;
    }

    /**
     *
     * Alchemy Format :
     *
     *
     <group>
     lokey=36
     hikey=36
     pitch_keycenter=36
     ampeg_decay=0.5
     ampeg_release=0.3

     <region> sample=Ambient-Kick1.wav
     lovel=1
     hivel=127

     *
     * @param instrument
     * @param outputDirectory
     */
    public void write(Instrument instrument, File outputDirectory) {

        FileWriter outFile = null;
        PrintWriter printWriter = null;
        instrumentFile = new File(outputDirectory,instrument.getName());

        try {
            outFile = new FileWriter(instrumentFile);
            printWriter = new PrintWriter(outFile);
            for (Zone zone : instrument.getZones()) {
                writeZone(printWriter, zone);
            }

        } catch (IOException e1) {
            logger.error("IO Exception",e1);
        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
        }

    }

    private void writeZone(PrintWriter printWriter, Zone zone) {
        printWriter.println("<group>");
        MidiRange noteRange = zone.getNoteRange();
        printWriter.println("lokey=" + noteRange.getLow().toString());
        printWriter.println("hikey=" + noteRange.getHigh().toString());
        printWriter.println("pitch_keycenter=" + noteRange.getLow().toString());
        printWriter.println("ampeg_decay=0.5");
        printWriter.println("ampeg_release=0.3");
        printWriter.println("");

        String relative = ResourceUtils.getRelativePath(instrumentFile, zone.getSample().getFile());

        // The SFZ spec says that sample paths *must* be relative, although some software handles absolute paths
        printWriter.println("<region> sample=" + relative);
        printWriter.println("lovel=" + zone.getVelocityRange().getLow().toString());
        printWriter.println("hivel=" + zone.getVelocityRange().getHigh().toString());
        if(zone.getSample().getLooping()) {
            printWriter.println("loop_mode=loop_sustain");
        } else {
            printWriter.println("loop_mode=one_shot");
        }

        printWriter.println("");
        printWriter.println("");
    }
}
