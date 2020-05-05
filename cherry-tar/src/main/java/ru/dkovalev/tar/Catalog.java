package ru.dkovalev.tar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Catalog implements Serializable {

    private static final long serialVersionUID = 2019_09_18L;

    private List<Entry> entries = new ArrayList<>();

    public Catalog() {
    }

    public void addFile(String name) {
        entries.add(new Entry(name, false));
    }

    public void addDirectory(String name) {
        entries.add(new Entry(name, true));
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public List<String> getFiles() {
        return entries.stream()
                .filter(Entry::isFile)
                .map(Entry::getName)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return entries.toString();
    }

    public static class Entry implements Serializable {
        private static final long serialVersionUID = 1;

        private final String name;
        private final boolean isDirectory;

        public Entry(String name, boolean isDirectory) {
            this.name = name;
            this.isDirectory = isDirectory;
        }

        public String getName() {
            return name;
        }

        public boolean isDirectory() {
            return isDirectory;
        }

        public boolean isFile() {
            return !isDirectory;
        }
    }
}
