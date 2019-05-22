package com.huawei.imbp.admin.util;

import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Log4j2
public class FileUtil {

    public List<File> getAllFilesMatchFileNameExtension(String dirName, String extension){

        File directory = new File(dirName);
        Path path = Paths.get(directory.getPath());
        File file = path.toFile();
        List<File> files = new ArrayList<>();
        Stream.of(file.list((pFile, pStr) -> pStr.endsWith(".json"))).forEach( p -> {
            files.add(new File(dirName+"//"+p));
        });

        return files;
    }
}
