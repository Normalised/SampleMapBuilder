package com.relivethefuture.dkb;

enum LayoutStyle {
    SOURCE("Source"),
    RELATIVE("Relative"),
    ABSOLUTE("Individual");

    String name;

    LayoutStyle(String t) {
        name = t;
    }

    static listNames() {
        [SOURCE.name,RELATIVE.name,ABSOLUTE.name]
    }

    static list() {
        [SOURCE,RELATIVE,ABSOLUTE]
    }

    static fromName(String name) {
        return list().find { it.name.equalsIgnoreCase(name) }
    }
}
