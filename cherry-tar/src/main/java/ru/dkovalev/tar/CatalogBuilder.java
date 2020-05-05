package ru.dkovalev.tar;

import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class CatalogBuilder extends SimpleFileVisitor<Path> {

    private Catalog catalog;

    public CatalogBuilder(Catalog catalog) {
        this.catalog = catalog;
    }

    public Catalog getCatalog() {
        return catalog;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        catalog.addFile(file.toString());
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
        catalog.addDirectory(dir.toString());
        return FileVisitResult.CONTINUE;
    }
}
