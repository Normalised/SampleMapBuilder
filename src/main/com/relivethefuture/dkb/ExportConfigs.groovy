package com.relivethefuture.dkb

import com.relivethefuture.formats.SamplerFormat
import com.relivethefuture.formats.ableton.DrumRackSamplersConfig
import com.relivethefuture.formats.ableton.DrumRackSimplersConfig
import com.relivethefuture.formats.ableton.SamplerZonesConfig
import com.relivethefuture.formats.reaktor.ReaktorConfig
import com.relivethefuture.formats.renoise.RenoiseConfig
import com.relivethefuture.formats.sfz.SfzConfig
import com.relivethefuture.formats.shortcircuit.ShortcircuitConfig
import com.relivethefuture.kitbuilder.output.GenericOutput
import com.relivethefuture.kitbuilder.output.OutputConfig

import java.util.prefs.Preferences

class ExportConfigs {

    static final Preferences PREFERENCES = Preferences.userNodeForPackage(ExportConfigs)

    Map<String, GenericOutput> configMap

    ReaktorConfig reaktor
    DrumRackSamplersConfig rackSamplers
    DrumRackSimplersConfig rackSimplers
    RenoiseConfig renoise
    SfzConfig sfz
    ShortcircuitConfig shortcircuit
    SamplerZonesConfig samplerZones

    GenericOutput genericOutput
    OutputConfig samples
    OutputLayout outputLayout

    public ExportConfigs() {
        genericOutput = new GenericOutput()
        samples = new OutputConfig()
        samples.setFormat(SamplerFormat.FILES)
        reaktor = new ReaktorConfig()
        rackSamplers = new DrumRackSamplersConfig()
        rackSimplers = new DrumRackSimplersConfig()
        samplerZones = new SamplerZonesConfig()
        renoise = new RenoiseConfig()
        shortcircuit = new ShortcircuitConfig()
        sfz = new SfzConfig()

        outputLayout = new OutputLayout()

        rackSamplers.active = false;
        rackSimplers.active = false;
        samplerZones.active = false;
        renoise.active = false
        sfz.active = false;

        configMap = [
            base:genericOutput,
            samples:samples,
            reaktor:reaktor,
            renoise:renoise,
            rackSamplers:rackSamplers,
            rackSimplers:rackSimplers,
            samplerZones:samplerZones,
            sfz:sfz,
            shortcircuit:shortcircuit
        ]

        updateFromPreferences()
    }

    def updateFromPreferences() {
        String defaultPath = System.getProperty("user.home")

        configMap.each { name, config ->
            config.setOutputDirectory(new File(PREFERENCES.get(name + "Absolute", defaultPath)))
            if(!name.equals("base")) {
                config.setRelativePath(PREFERENCES.get(name + "Relative", ((OutputConfig) config).format.type() ))
                config.setActive(PREFERENCES.get(name + "Active", "true").equalsIgnoreCase("true"))
            }
            if(name.equalsIgnoreCase("reaktor")) {
                Integer reaktorRoot = Integer.parseInt(PREFERENCES.get("reaktorRoot","-1"))
                if(reaktorRoot >= 0) {
                    config.setRoot(reaktorRoot)
                }
            }
        }

        outputLayout.layoutStyle = LayoutStyle.fromName(PREFERENCES.get("layoutStyle",LayoutStyle.RELATIVE.name))
        outputLayout.outputStructureDepth = Integer.parseInt(PREFERENCES.get("outputDepth","0"))
        outputLayout.filenameDepth = Integer.parseInt(PREFERENCES.get("filenameDepth","1"))
    }

    def updatePrefs() {
        configMap.each { name, config ->
            PREFERENCES.put(name + "Absolute", config.getOutputDirectory().getAbsolutePath())
            if(!name.equals("base")) {
                PREFERENCES.put(name + "Relative", config.getRelativePath())
                PREFERENCES.put(name + "Active", config.active ? "true" : "false")
            }
            if(name.equalsIgnoreCase("reaktor")) {
                if(config.getRoot() != null) {
                    PREFERENCES.put("reaktorRoot",config.getRoot().toString())
                }
            }
        }

        PREFERENCES.put("layoutStyle",outputLayout.layoutStyle.name)
        PREFERENCES.put("outputDepth",outputLayout.outputStructureDepth.toString())
        PREFERENCES.put("filenameDepth",outputLayout.filenameDepth.toString())
    }
}
