package org.rsrg.mixfix.util;

public record Version(int major, int minor, int patch) {

    public static final Version Current = new Version(0, 1, 0);

    @Override public String toString() {
        return String.format("%d.%d.%d", major, minor, patch);
    }
}
