package me.kikugie.stt;

import java.util.List;

public enum StackSize {
    ALL("All Types", 64, 16, 1),
    STACKABLES("Only Stackables", 64, 16),
    STACKABLE_64("Only 64 Stackables", 64),
    STACKABLE_16("Only 16 Stackables", 16),
    UNSTACKABLES("Unstackables", 1);

    private final String title;
    private final List<Integer> sizes;

    StackSize(String name, Integer... sizes) {
        this.title = name;
        this.sizes = List.of(sizes);
    }

    public String getTitle() {
        return title;
    }

    public List<Integer> getSizes() {
        return sizes;
    }

    @Override
    public String toString() {
        return "StackSize{" + title + "}";
    }
}
