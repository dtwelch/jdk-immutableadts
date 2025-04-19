package org.rsrg.immutableadts;

// nel = "non empty lists"
public record VNel<T>(T x, VList<T> xs) {

    /** Returns the number of elements in {@code this} (always at least one). */
    public int length() {
        return 1 + xs.length();
    }



}
