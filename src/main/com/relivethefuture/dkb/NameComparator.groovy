package com.relivethefuture.dkb

class NameComparator implements Comparator<String> {
    int compare(String o1, String o2) {
        return(o1.compareToIgnoreCase(o2))
    }
}
