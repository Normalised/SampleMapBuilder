package com.relivethefuture.dkb

import ca.odell.glazedlists.GlazedLists
import ca.odell.glazedlists.gui.AdvancedTableFormat
import ca.odell.glazedlists.gui.WritableTableFormat
import com.relivethefuture.generic.Sample

class SampleTableFormat implements AdvancedTableFormat<Sample>,WritableTableFormat<Sample> {

    def columnNames = ["Note","Name", "Size (k)","Exclude"]
    def classes = [Integer.class, String.class,Integer.class,Boolean.class]

    Class getColumnClass(int index) {
        return classes[index]
    }

    Comparator getColumnComparator(int index) {
        if(index == 3) {
            return SampleComparator.EXCLUDED.tableComparator
        } else if(index == 2) {
            return SampleComparator.SIZE.tableComparator
        } else if(index == 1) {
            return SampleComparator.NAME.tableComparator
        } else {
            return SampleComparator.NOTE.tableComparator
        }
    }

    boolean isEditable(Sample sample, int index) {
        return (index == 3)
    }

    Sample setColumnValue(Sample sample, Object newValue, int index) {
        if(index == 3) {
            sample.exclude = newValue
        }
        return sample
    }

    int getColumnCount() {
        return columnNames.size()
    }

    String getColumnName(int index) {
        return columnNames[index]
    }

    Object getColumnValue(Sample sample, int index) {
        if(index == 3) {
            return sample.exclude
        } else if(index == 2) {
            return Math.floor(sample.size / 1024) as Integer
        } else if(index == 1) {
            return sample.name
        } else {
            return sample.root
        }

    }
}
