package com.relivethefuture.dkb

import com.relivethefuture.generic.Sample

class RootNoteComparator implements Comparator<Sample> {
    int compare(Sample o1, Sample o2) {
        return o1.root.compareTo(o2.root)
    }
}
