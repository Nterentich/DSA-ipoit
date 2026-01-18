package by.it.group410971.teterich.lesson15;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SourceScannerC {
    public static void main(String[] args) {
        String root = System.getProperty("user.dir") + "/src/by/it/a_khmelev/";
        List<File> files = new ArrayList<>();
        scanDirectory(new File(root), files);
        for (File file : files) {
            System.out.println(file.getPath().replace("\\", "/"));
        }
    }

    private static void scanDirectory(File dir, List<File> result) {
        if (dir.isDirectory()) {
            File[] list = dir.listFiles();
            if (list != null) {
                for (File f : list) {
                    if (f.isDirectory()) {
                        scanDirectory(f, result);
                    } else if (f.getName().endsWith(".java")) {
                        result.add(f);
                    }
                }
            }
        }
    }
}