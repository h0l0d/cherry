package ru.dkovalev.tar;

import java.io.IOException;
import java.util.List;

public interface TarArchiver {

    void pack(String archiveName, List<String> fileNames) throws IOException;
    void unpack(String archiveName, String directory) throws IOException;
    void list(String archiveName) throws IOException;
}
