package by.it.group410971.teterich.lesson15;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class SourceScannerA {

    // Класс для хранения информации о файле
    static class FileInfo implements Comparable<FileInfo> {
        String relativePath;
        int size;

        FileInfo(String relativePath, int size) {
            this.relativePath = relativePath;
            this.size = size;
        }

        @Override
        public int compareTo(FileInfo other) {
            if (this.size != other.size) {
                return Integer.compare(this.size, other.size); // сортировка по размеру
            }
            return this.relativePath.compareTo(other.relativePath); // лексикографически
        }
    }

    public static void main(String[] args) {
        String src = System.getProperty("user.dir") + File.separator + "src" + File.separator;
        Path srcPath = Paths.get(src);

        List<FileInfo> fileInfos = new ArrayList<>();

        try {
            // Рекурсивный обход всех файлов
            Files.walkFileTree(srcPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (file.toString().endsWith(".java")) {
                        try {
                            // Читаем содержимое файла с обработкой ошибок кодировки
                            String content = readFileWithFallback(file);

                            // Проверяем, не является ли файл тестом
                            if (content.contains("@Test") || content.contains("org.junit.Test")) {
                                return FileVisitResult.CONTINUE;
                            }

                            // Обрабатываем содержимое
                            String processedContent = processContent(content);

                            // Получаем относительный путь
                            String relativePath = srcPath.relativize(file).toString();

                            // Рассчитываем размер в байтах (UTF-8)
                            int size = processedContent.getBytes(StandardCharsets.UTF_8).length;

                            fileInfos.add(new FileInfo(relativePath, size));

                        } catch (IOException e) {
                            // Игнорируем файлы с ошибками чтения
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    // Игнорируем файлы, к которым нет доступа
                    return FileVisitResult.CONTINUE;
                }
            });

            // Сортируем файлы
            Collections.sort(fileInfos);

            // Выводим результат
            for (FileInfo fileInfo : fileInfos) {
                System.out.println(fileInfo.size + " " + fileInfo.relativePath);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Чтение файла с обработкой различных кодировок
    private static String readFileWithFallback(Path file) throws IOException {
        // Сначала пробуем UTF-8
        try {
            return new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
        } catch (Exception e) {
            // Если UTF-8 не сработал, пробуем другие кодировки
            Charset[] charsets = {
                    StandardCharsets.ISO_8859_1,
                    Charset.forName("Windows-1251"),
                    StandardCharsets.US_ASCII
            };

            for (Charset charset : charsets) {
                try {
                    return new String(Files.readAllBytes(file), charset);
                } catch (Exception ex) {
                    continue;
                }
            }

            // Если ничего не помогло, возвращаем пустую строку
            return "";
        }
    }

    // Обработка содержимого файла
    private static String processContent(String content) {
        StringBuilder result = new StringBuilder();
        String[] lines = content.split("\n");

        for (String line : lines) {
            // Пропускаем строки с package и import
            String trimmedLine = line.trim();
            if (trimmedLine.startsWith("package") || trimmedLine.startsWith("import")) {
                continue;
            }
            result.append(line).append("\n");
        }

        // Удаляем символы с кодом < 33 в начале и конце
        String processed = result.toString();

        // Удаляем в начале
        int start = 0;
        while (start < processed.length() && processed.charAt(start) < 33) {
            start++;
        }

        // Удаляем в конце
        int end = processed.length() - 1;
        while (end >= start && processed.charAt(end) < 33) {
            end--;
        }

        if (start > end) {
            return "";
        }

        return processed.substring(start, end + 1);
    }
}