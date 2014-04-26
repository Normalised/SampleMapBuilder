package com.relivethefuture.dkb

import ca.odell.glazedlists.impl.sort.BooleanComparator
import ca.odell.glazedlists.impl.sort.ComparableComparator
import ca.odell.glazedlists.impl.sort.ReverseComparator

public enum SampleComparator {

    SIZE("size",new ComparableComparator(), { a,b -> a.size <=> b.size } as Comparator),
    NAME("name",new ComparableComparator(), { a,b -> a.name <=> b.name} as Comparator),
    EXCLUDED("excluded",new BooleanComparator(), { a,b -> a.excluded <=> b.excluded} as Comparator),
    NOTE("note",new RootNoteComparator(), new RootNoteComparator())

    private String id
    def Comparator tableComparator
    def Comparator sampleComparator

    SampleComparator(String id, Comparator tableComp, Comparator sampleComp) {
        this.id = id
        this.tableComparator = tableComp
        this.sampleComparator = sampleComp
    }

    static list() {
        return [SIZE,NAME,EXCLUDED,NOTE]
    }

    static byTableColumn(Integer column, Boolean reverse) {
        Comparator c = NAME.sampleComparator
        switch (column) {
            case 0: c = NOTE.sampleComparator; break;
            case 1: c = NAME.sampleComparator; break;
            case 2: c = SIZE.sampleComparator; break;
            case 3: c = EXCLUDED.sampleComparator; break;
        }

        if(reverse) {
            return new ReverseComparator(c)
        } else {
            return c
        }
    }
}