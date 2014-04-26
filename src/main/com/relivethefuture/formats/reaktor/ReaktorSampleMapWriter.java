package com.relivethefuture.formats.reaktor;

import com.elharo.io.LittleEndianOutputStream;
import com.relivethefuture.generic.Instrument;
import com.relivethefuture.generic.Zone;
import com.relivethefuture.kitbuilder.writers.BasicInstrumentWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReaktorSampleMapWriter extends BasicInstrumentWriter {

    private static Logger logger = LoggerFactory.getLogger(ReaktorSampleMapWriter.class);

    private Map<String, String> osxPathMap;
    private File outputDir;
    private boolean isWindows;
    private boolean isOSX;
    private ArrayList<File> volumes;
    private File osxSystemVolume;
    private File instrumentFile;

    public ReaktorSampleMapWriter() {
        osxPathMap = new HashMap<String, String>();
        String osName = System.getProperty("os.name").toLowerCase();
        isWindows = osName.startsWith("win");
        isOSX = osName.startsWith("mac os x");
        if(isOSX) {
            logger.debug("Export reaktor for OSX");
            prepareVolumes();
        } else {
            logger.debug("Export reaktor for Windows");
        }
    }

    private void prepareVolumes() {
        File volumesDir = new File("/Volumes");

        File[] volumeFiles = volumesDir.listFiles();
        volumes = new ArrayList<File>();
        for (File vol : volumeFiles) {
            if(vol.getName().contains("Macintosh")) {
                osxSystemVolume = vol;
            } else {
                volumes.add(vol);
            }
        }
    }

    public void write(Instrument instrument,File outputDir) {

        logger.debug("Reaktor writing " + instrument.getName() + " to " + outputDir.getAbsolutePath());
        this.outputDir = outputDir;

        File outputFile = new File(outputDir, instrument.getName());

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(outputFile);
            LittleEndianOutputStream data = new LittleEndianOutputStream(fos);
            write(instrument,data);
            data.flush();
            data.close();
        } catch (FileNotFoundException e) {
            logger.error("File not found for output stream " + outputFile.getAbsolutePath());
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     typedef struct {
         uint    zeros;
         uint    h7702; //77 02 00 00
         char    NIMapFile[9];
         PATH    mapFilePath;
         char    mapp[4];
         uint    h0c; // 0c 00 00 00
         uint    one; // 01 00 00 00
         uint    one; // 01 00 00 00
         uint    zeros;
         uint    zeros;
         uint    numSamples; // 03 00 00 00
     } SAMPLEMAPHEADER;

     * @param instrument
     * @param data
     * @throws IOException
     */

    private void write(Instrument instrument, LittleEndianOutputStream data) throws IOException {
        instrumentFile = new File(outputDir,instrument.getName());

        // Header
        data.writeHex("00000000");
        data.writeHex("77020000");
        // 9 Bytes Text : NIMapFile
        data.writeBytes("NIMapFile");
        // Map File Path : String

        // TODO : add OSX 'localhost' to file url

        writePath(data,instrumentFile.getAbsolutePath());

        data.writeBytes("mapp");
        data.writeHex("0c000000");
        data.writeInt(1);
        data.writeInt(1);
        data.writeInt(0);
        data.writeInt(0);

        // Number of sampleLists in the map
        data.writeInt(instrument.getZones().size());

        for (Zone zone : instrument.getZones()) {
            Zone entry = zone;
            writeEntry(entry, data);
        }
    }

    /**
     *
     typedef struct {
        // 02 00 00 00 00 03 00 00 00 11 11
         byte    header[11];
         string  url;

     } URL_STYLE_FILENAME;

     typedef struct {
         uint length;
         wchar_t name[length];
     } UTF_8_FILENAME;

     typedef struct {
         URL_STYLE_FILENAME url;
         UTF_8_FILENAME utfName;
     } PATH;

     * @param data
     * @param path
     */
    private void writePath(LittleEndianOutputStream data, String path) throws IOException {
        data.writeHex("0200000000030000001111");
        logger.debug("Write Path : " + path);
        if(isOSX) {
            data.writeNullTerminatedString("file://localhost" + path);
            String fixed = fixPathForCurrentOS(path);
            data.writeFakeUTF(fixed);
        } else {
            String fp = path.replace("\\","/");
            data.writeNullTerminatedString("file:///" + fp);
            data.writeFakeUTF(path);
        }


    }

    /**
     *
     typedef struct {
         PATH    samplePath;
         uint    zeros;
         char    entr[4];
         uint    h54;
         uint    h2;
         uint    lowNote;
         uint    highNote;
         uint    lowVel;
         uint    highVel;
         uint    rootNote;
         uint    zeros;
         float   tune;
         float   volume;
         float   pan;
         uint    FFzero[3];
         uint    isLooped;
         uint    zeros;
         uint    one;
         uint    loopStart;
         uint    loopEnd;
         uint    zeros;
         uint    fiftyfive;
         uint    stuff;
     } SAMPLE;

     * @param entry
     * @param data
     * @throws IOException
     */
    private void writeEntry(Zone entry, LittleEndianOutputStream data) throws IOException {

        File sampleFile = entry.getSample().getFile();
        writePath(data, sampleFile.getAbsolutePath());
        data.writeInt(0);
        data.writeBytes("entr");
        // # Unknown byte sequence, same in all files so far
        data.writeHex("54000000");
        data.writeHex("02000000");

        // Key zone / Velocity / Root Key stuff
        data.writeInt(entry.getNoteRange().getLow());
        data.writeInt(entry.getNoteRange().getHigh());
        data.writeInt(entry.getVelocityRange().getLow());
        data.writeInt(entry.getVelocityRange().getHigh());
        data.writeInt(entry.getSample().getRoot());

        // unknown
        //data.writeHex("00000000");
        data.writeInt(0);

        // Tune. 32 bit signed float
        data.writeFloat(entry.getSample().getTune());
        // ? Gain. Unknown format, probably 32 bit signed float
        data.writeFloat(entry.getSample().getVolume());
        // ? Pan. Unknown format, probably 32 bit signed float
        data.writeFloat(entry.getSample().getPan());

        // Unknown byte data
        data.writeHex("FFFFFFFF");
        data.writeHex("FFFFFFFF");
        //data.writeHex("00000000");
        data.writeInt(0);

        // ? loop on. Unknown format, assume int for now
        data.writeInt(entry.getSample().getLooping() ? 1 : 0);

        // More unknown data
        //data.writeHex("00000000");
        data.writeInt(0);
        //data.writeHex("01000000");
        data.writeInt(1);

        // Loop start : 32 bit int
        data.writeInt(entry.getSample().getLoopSettings().getLoopStart());
        data.writeInt(entry.getSample().getLoopSettings().getLoopEnd());

        // Unknown
        data.writeInt(0);
        //data.writeHex("00000000");

        // End of sample data, seems identical in all entries so far
        data.writeHex("55000000");
        //data.writeHex("06426200");
        data.writeInt(0);
    }

    /**
     * On windows the directory name has to have the slashes replaced
     *
     * On OSX the path names need to have the Volume name in front. By default OSX
     * treats paths on the system drive as 'special' and doesn't refer to them
     * via their Volume, whereas Reaktor does.
     * Also slashes are replaced with the old school mac : style
     *
     * @param path
     * @return
     */
    private String fixPathForCurrentOS(String path) {
        logger.debug("Fix path " + path);
        if (isWindows) {
            path = path.replace('/', '\\');
        } else if (isOSX) {
            if (path.startsWith("/Volumes")) {
                path = path.substring(8);
            } else {
                // Check if the path is cached
                if (osxPathMap.containsKey(path))
                    return osxPathMap.get(path);

                File sampleFile;

                Boolean found = false;
                // First check system volume
                if(osxSystemVolume != null) {
                    sampleFile = new File(osxSystemVolume, path);
                    if(sampleFile.exists()) {
                        osxPathMap.put(path, osxSystemVolume.getName() + path);
                        path = osxSystemVolume.getName() + path;
                        found = true;
                    }
                }

                if(!found) {
                    File volumesDir = new File("/Volumes");
                    File[] volumes = volumesDir.listFiles();
                    for (File vol : volumes) {
                        sampleFile = new File(vol, path);
                        if (sampleFile.exists()) {
                            // Cache the result to save some time later
                            osxPathMap.put(path, vol.getName() + path);
                            path = vol.getName() + path;
                            break;
                        }
                    }
                }
            }

            path = path.replace('/', ':');

            if (path.charAt(0) == ':') {
                path = path.substring(1);
            }
        }

        return path;
    }

}
