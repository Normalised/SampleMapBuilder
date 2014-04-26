package com.relivethefuture.dkb

import com.relivethefuture.FileSystemScanner
import com.relivethefuture.ResourceUtils
import com.relivethefuture.generic.Instrument
import com.relivethefuture.generic.Sample
import com.relivethefuture.io.FileUtils
import com.relivethefuture.kitbuilder.KitFileNameGenerator
import com.relivethefuture.kitbuilder.builder.MapBuilder
import com.relivethefuture.kitbuilder.model.FolderOfSamples
import com.relivethefuture.kitbuilder.output.OutputConfig
import com.relivethefuture.kitbuilder.writers.InstrumentWriter
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.nio.channels.FileChannel

/**
 * Created by martin on 05/06/13 at 17:10
 *
 */
class Exporter {

    private final Logger log = LoggerFactory.getLogger(Exporter.class);

    List<FolderOfSamples> sourceFolders
    List<OutputConfig> outputConfigs
    List<Instrument> instruments
    Integer itemIndex
    Iterator<OutputConfig> outputConfigIterator
    MapBuilder mapBuilder
    ExportConfigs exportConfigs

    KitFileNameGenerator kitFileNameGenerator

    File sourceRoot
    LayoutStyle layoutStyle

    boolean copySamplesFirst
    boolean samplesCopied

    // How many configs to export (including sample config)
    Integer exportItemCount = 0

    public Exporter() {
        kitFileNameGenerator = new KitFileNameGenerator()
        mapBuilder = new MapBuilder()
    }

    def start(FileSystemScanner scanner, ExportConfigs configs) {
        exportConfigs = configs

        sourceFolders = scanner.getItems()
        // apply sorting to all source folders
        sourceFolders.each { FolderOfSamples folderOfSamples ->
            if(folderOfSamples.sampleComparator) {
                log.debug("Sorting " + folderOfSamples.name + " with " + folderOfSamples.sampleComparator)
                folderOfSamples.samples.sort(folderOfSamples.sampleComparator)
            }
        }
        sourceRoot = scanner.getRootDirectory()
        instruments = null;
        itemIndex = 0
        if(outputConfigs == null) {
            outputConfigs = new ArrayList<OutputConfig>()
        } else {
            outputConfigs.clear()
        }

        copySamplesFirst = false
        samplesCopied = false

        exportItemCount = 0

        exportConfigs.configMap.each { name, config ->
            if((config instanceof OutputConfig) && config.getActive()) {
                if(config == exportConfigs.samples && (layoutStyle != LayoutStyle.SOURCE)) {
                    // Samples need copying first
                    copySamplesFirst = true
                    exportItemCount++
                } else {
                    log.debug("Adding active config " + name)
                    outputConfigs.add(config);
                    exportItemCount++
                }
            } else {
                log.debug("Skipping config : " + name)
            }
        }
        outputConfigIterator = outputConfigs.iterator()
        layoutStyle = exportConfigs.outputLayout.layoutStyle

    }

    def doNext() {
        if(copySamplesFirst && !samplesCopied) {
            copySamples(exportConfigs.samples)
            itemIndex++
            samplesCopied = true
            return ["Samples",(itemIndex * 100f) / exportItemCount]
        } else if(!copySamplesFirst) {
            log.debug("Not copying samples")
            // make sure that the FOS directory and the sample file are pointing at the right place
            // This is only needed when there has been an export with copied samples and then the sample copy option is turned off

            sourceFolders.each { FolderOfSamples folderOfSamples ->
                if(folderOfSamples.directory != null && (folderOfSamples.directory.absolutePath != folderOfSamples.sourceDirectory.absolutePath)) {
                    folderOfSamples.directory = folderOfSamples.sourceDirectory
                    folderOfSamples.samples.each { Sample sample ->
                        sample.file = new File(folderOfSamples.sourceDirectory, sample.file.name)
                    }
                } else {
                    folderOfSamples.directory = folderOfSamples.sourceDirectory
                }
            }
        }

        if(!outputConfigIterator.hasNext()) {
            log.debug("No more configs")
            return ["Finished",100f]
        }

        OutputConfig outputConfig = outputConfigIterator.next()
        log.debug("Next Config : " + outputConfig.format.name())
        writeInstrument(outputConfig)
        itemIndex++;

        return [outputConfig.format.name(), (itemIndex * 100f) / exportItemCount];
    }

    /**
     * Copying samples will always match the input directory structure, or samples may get overwritten
     * @param copySamplesConfig
     */
    private void copySamples(OutputConfig copySamplesConfig) {
        log.debug("Copying samples")

        // Base Path is root of destination
        File outputDirectory = exportConfigs.genericOutput.getOutputDirectory()

        if(layoutStyle == LayoutStyle.ABSOLUTE) {
            outputDirectory = copySamplesConfig.getOutputDirectory()
        } else if(layoutStyle == LayoutStyle.RELATIVE) {
            outputDirectory = new File(outputDirectory, copySamplesConfig.getRelativePath())
        }

        log.debug("Source Root : " + sourceRoot)
        log.debug("Output Base : " + outputDirectory)
        File resolvedOutputDirectory
        String sampleRelative
        log.debug("Copying samples for " + sourceFolders.size() + " source folders")

        sourceFolders.each { FolderOfSamples folderOfSamples ->
            log.debug("FOS path : " + folderOfSamples.sourceDirectory)
            // Get path from scan root to actual folder of source samples
            if(folderOfSamples.sourceDirectory.getAbsolutePath() != sourceRoot.getAbsolutePath()) {

                sampleRelative = ResourceUtils.getRelativePath(sourceRoot,folderOfSamples.sourceDirectory)

                log.debug("Relative dir for source samples : " + sampleRelative)
                resolvedOutputDirectory = new File(outputDirectory, sampleRelative);
                log.debug("Resolved Output " + resolvedOutputDirectory + " for " + folderOfSamples.sourceDirectory)
                if(!resolvedOutputDirectory.exists()) {
                    resolvedOutputDirectory.mkdirs();
                }
            } else {
                resolvedOutputDirectory = outputDirectory
            }

            File original
            File copy
            folderOfSamples.samples.each { Sample sample ->
                // Rather than use the sample.file reference, resolve via the paths
                // because after a copy the sample.file reference points to the copy, not the original
                // then if you try and copy again it'll try and copy the file to itself
                // instead of looping over every sample and resetting the reference do this.
                original = new File(folderOfSamples.sourceDirectory, sample.file.name)
                copy = new File(resolvedOutputDirectory, sample.file.name)
                log.debug("Copy from " + original.getAbsolutePath() + " to " + copy.getAbsolutePath())
                if(FileUtils.copyFile(original, copy, true)) {
                    log.debug("File copied OK")
                } else {
                    log.debug("Copy Failed")
                }
                // Update sample to point at copy
                sample.file = copy
            }
            // Update FOS to point at copy
            folderOfSamples.directory = resolvedOutputDirectory
        }
    }

    private void writeInstrument(OutputConfig outputConfig) {
        log.debug("Building output for format " + outputConfig.getFormat().name());

        kitFileNameGenerator.setLayout(exportConfigs.outputLayout)
        kitFileNameGenerator.setConfig(outputConfig)

        instruments = mapBuilder.build(outputConfig, kitFileNameGenerator, sourceFolders);

        log.debug("Got " + instruments.size() + " maps");

        InstrumentWriter writer = outputConfig.getWriter();

        File outputDirectory
        File basePath = exportConfigs.genericOutput.getOutputDirectory()
        log.debug("Base Path : " + basePath)
        File absolutePath = outputConfig.getOutputDirectory()
        log.debug("Absolute Path : " + absolutePath)

        for(Instrument instrument : instruments) {
            log.debug("Writing output for format " + outputConfig.getFormat().name());

            if(layoutStyle == LayoutStyle.ABSOLUTE) {
                basePath = absolutePath
            }
            outputDirectory = PathUtils.getOutputDirectoryForLayout(basePath, instrument.getSourceDirectory(), exportConfigs.outputLayout, outputConfig.getRelativePath())

            log.debug("Got output directory : " + outputDirectory)
            if(!outputDirectory.exists()) {
                outputDirectory.mkdirs();
            }

            writer.write(instrument,outputDirectory);
        }
    }
}
