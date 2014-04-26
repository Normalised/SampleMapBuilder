package com.relivethefuture.formats;

public enum SamplerFormat {
    REAKTOR("Reaktor","map","reaktor"),
    ABLETON_DRUM_RACK_SAMPLERS("Ableton Drum Rack with Samplers","adg","rackSamplers"),
    ABLETON_DRUM_RACK_SIMPLERS("Ableton Drum Rack with Simplers","adg","rackSimplers"),
    ABLETON_SAMPLER("Ableton Sampler","adv","samplerZones"),
    RENOISE_INSTRUMENT("Renoise Instrument","xrni","renoise"),
    SHORTCIRCUIT("Shortcircuit","scm","shortcircuit"),
    EXS24("EXS24","","exs"),
    FILES("FILES","",""),
    SFZ("SFZ","sfz","sfz");

    private String title;
    private String type;
    private String extension;

    SamplerFormat(String title, String extension, String type) {
        this.type = type;
        this.extension = extension;
        this.title = title;
    }

    public String type() {
        return type;
    }
    public String extension() {
        return extension;
    }

    public String title() {
        return title;
    }
}
