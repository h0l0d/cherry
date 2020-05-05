package ru.dkovalev.tar;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class Java7TarArchiver implements TarArchiver {

    @Override
    public void pack(String archiveName, List<String> fileNames) throws IOException {
        Catalog catalog = createCatalog(fileNames);
        FileOutputStream fileOutputStream = new FileOutputStream(archiveName);
        try (ObjectOutputStream oos = new ObjectOutputStream(fileOutputStream)) {
            oos.writeObject(catalog);
            for (String entry : catalog.getFiles()) {
                long size = Files.size(Paths.get(entry));
                oos.writeLong(size);
                oos.flush();
                long bytesWritten = Files.copy(Paths.get(entry), fileOutputStream);
                if (size != bytesWritten) {
                    throw new RuntimeException(String.format("File size mismatch on write: expected=%d, actual=%d", size, bytesWritten));
                }
            }
        }
    }

    private Catalog createCatalog(List<String> fileNames) throws IOException {
        Catalog catalog = new Catalog();
        CatalogBuilder catalogBuilder = new CatalogBuilder(catalog);
        for (String fileName : fileNames) {
            Path filePath = Paths.get(fileName);
            if (Files.isDirectory(filePath)) {
                Files.walkFileTree(filePath, catalogBuilder);
            } else {
                catalog.addFile(fileName);
            }
        }
        return catalog;
    }

    @Override
    public void unpack(String archiveName, String directory) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(archiveName);
        try (ObjectInputStream ois = new ObjectInputStream(fileInputStream)) {
            Catalog catalog = (Catalog) ois.readObject();
            for (Catalog.Entry entry : catalog.getEntries()) {
                if (entry.isDirectory()) {
                    Files.createDirectory(Paths.get(directory, entry.getName()));
                } else {
                    long size = ois.readLong();
                    long bytesRead = copyFile(fileInputStream, Paths.get(directory, entry.getName()), size);
                    if (size != bytesRead) {
                        throw new RuntimeException(String.format("File size mismatch on read: expected=%d, actual=%d", size, bytesRead));
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private long copyFile(InputStream is, Path target, long size) throws IOException {
        byte[] buffer = new byte[8192];
        long bytesRead = 0;
        try (OutputStream ostream = Files.newOutputStream(target, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
            while (bytesRead < size) {
                int bytesToRead = (int) Math.min(buffer.length, size);
                int n = is.read(buffer, 0, bytesToRead);
                if (n == -1) {
                    break;
                }
                ostream.write(buffer, 0, n);
                bytesRead += n;
            }
        }
        return bytesRead;
    }

    @Override
    public void list(String archiveName) throws IOException {
        Catalog catalog;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archiveName))) {
            catalog = (Catalog) ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        catalog.getEntries().stream()
                .map(Catalog.Entry::getName)
                .forEach(System.out::println);
    }

}
