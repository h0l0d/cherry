package ru.dkovalev.tar;

import java.io.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Usages:
 * SimpleTar -c ARCHIVE_NAME FILE+
 *     # Create archive with given name for FILEs
 *
 * SimpleTar -t ARCHIVE_NAME
 *      # List all files in archive
 *
 * SimpleTar -x ARCHIVE_NAME [DIR]
 *      # Extract all files from archive to the current dir or to the given dir.
 */
public class SimpleTar {

    private TarArchiver archiver = new Java7TarArchiver();

    public static void main(String[] args) throws IOException {
        SimpleTar simpleTar = new SimpleTar();
        String action = args[0];
        switch (action) {
            case "-c":
                simpleTar.create(args);
                break;
            case "-x":
                simpleTar.extract(args);
                break;
            case "-t":
                simpleTar.list(args);
                break;
        }
    }

    public void create(String[] args) throws IOException {
        if (args.length < 3) {
            throw new IllegalArgumentException();
        }
        String archiveName = args[1];
        List<String> fileNames = Arrays.stream(args, 2, args.length).collect(Collectors.toList());
        archiver.pack(archiveName, fileNames);
    }

    public void extract(String[] args) throws IOException {
        if (args.length < 2 || args.length > 3) {
            throw new IllegalArgumentException();
        }
        String archiveName = args[1];
        String directory;
        if (args.length == 3) {
            directory = args[2];
            Files.createDirectory(Paths.get(directory));
        } else {
            directory = "./";
        }
        archiver.unpack(archiveName, directory);
    }


    public void list(String[] args) throws IOException {
        archiver.list(args[1]);
    }
}
