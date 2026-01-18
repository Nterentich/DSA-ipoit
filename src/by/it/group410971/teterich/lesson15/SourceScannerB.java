package by.it.group410971.teterich.lesson15;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class SourceScannerB {

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
                return Integer.compare(this.size, other.size);
            }
            return this.relativePath.compareTo(other.relativePath);
        }
    }

    public static void main(String[] args) {
        String src = System.getProperty("user.dir") + File.separator + "src" + File.separator;
        Path srcPath = Paths.get(src);

        List<FileInfo> fileInfos = new ArrayList<>();

        try {
            Files.walkFileTree(srcPath, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (file.toString().endsWith(".java")) {
                        try {
                            String content = readFileWithFallback(file);

                            if (content.contains("@Test") || content.contains("org.junit.Test")) {
                                return FileVisitResult.CONTINUE;
                            }

                            String processedContent = processContent(content);
                            String relativePath = srcPath.relativize(file).toString();
                            int size = processedContent.getBytes(StandardCharsets.UTF_8).length;

                            fileInfos.add(new FileInfo(relativePath, size));

                        } catch (IOException e) {
                            // Игнорируем ошибки чтения
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    return FileVisitResult.CONTINUE;
                }
            });

            Collections.sort(fileInfos);

            for (FileInfo fileInfo : fileInfos) {
                System.out.println(fileInfo.size + " " + fileInfo.relativePath);
            }

        } catch (IOException e) {
            System.err.println("Error walking file tree: " + e.getMessage());
        }
    }

    private static String readFileWithFallback(Path file) throws IOException {
        // Пробуем разные кодировки
        List<Charset> charsets = Arrays.asList(
                StandardCharsets.UTF_8,
                StandardCharsets.ISO_8859_1,
                Charset.forName("Windows-1251"),
                Charset.forName("CP1252"),
                StandardCharsets.US_ASCII
        );

        for (Charset charset : charsets) {
            try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                return content.toString();
            } catch (IOException e) {
                // Пробуем следующую кодировку
                continue;
            }
        }

        return ""; // Если ни одна кодировка не подошла
    }

    private static String processContent(String content) {
        StringBuilder result = new StringBuilder();

        // Режимы парсера
        final int NORMAL = 0;
        final int LINE_COMMENT = 1;
        final int BLOCK_COMMENT = 2;
        final int STRING = 3;
        final int CHAR = 4;

        int mode = NORMAL;
        int i = 0;
        boolean skipPackageImport = false;
        boolean inPackageImport = false;

        while (i < content.length()) {
            char c = content.charAt(i);

            switch (mode) {
                case NORMAL:
                    if (c == '/' && i + 1 < content.length()) {
                        char next = content.charAt(i + 1);
                        if (next == '/') {
                            mode = LINE_COMMENT;
                            i += 2;
                            continue;
                        } else if (next == '*') {
                            mode = BLOCK_COMMENT;
                            i += 2;
                            continue;
                        }
                    } else if (c == '"') {
                        mode = STRING;
                    } else if (c == '\'') {
                        mode = CHAR;
                    } else if (c == 'p' && i + 7 < content.length()) {
                        if (content.startsWith("package", i)) {
                            skipPackageImport = true;
                            inPackageImport = true;
                        }
                    } else if (c == 'i' && i + 6 < content.length()) {
                        if (content.startsWith("import", i)) {
                            skipPackageImport = true;
                            inPackageImport = true;
                        }
                    }

                    if (!skipPackageImport && c >= 33) {
                        result.append(c);
                    }
                    break;

                case LINE_COMMENT:
                    if (c == '\n') {
                        mode = NORMAL;
                        skipPackageImport = false;
                    }
                    break;

                case BLOCK_COMMENT:
                    if (c == '*' && i + 1 < content.length() && content.charAt(i + 1) == '/') {
                        mode = NORMAL;
                        i++; // Пропускаем '/'
                        skipPackageImport = false;
                    }
                    break;

                case STRING:
                    if (c == '"' && (i == 0 || content.charAt(i - 1) != '\\')) {
                        mode = NORMAL;
                    }
                    if (!skipPackageImport) {
                        result.append(c);
                    }
                    break;

                case CHAR:
                    if (c == '\'' && (i == 0 || content.charAt(i - 1) != '\\')) {
                        mode = NORMAL;
                    }
                    if (!skipPackageImport) {
                        result.append(c);
                    }
                    break;
            }

            // Если мы в package/import и нашли конец строки, сбрасываем флаг
            if (inPackageImport && c == '\n') {
                skipPackageImport = false;
                inPackageImport = false;
            }

            i++;
        }

        // Разбиваем на строки, удаляем пустые и символы < 33
        String[] lines = result.toString().split("\n");
        StringBuilder finalResult = new StringBuilder();

        for (String line : lines) {
            // Удаляем символы < 33 в начале и конце строки
            int start = 0;
            while (start < line.length() && line.charAt(start) < 33) {
                start++;
            }

            int end = line.length() - 1;
            while (end >= start && line.charAt(end) < 33) {
                end--;
            }

            if (start <= end) {
                String trimmedLine = line.substring(start, end + 1);
                if (!trimmedLine.isEmpty()) {
                    finalResult.append(trimmedLine).append("\n");
                }
            }
        }

        // Удаляем символы < 33 в начале и конце всего текста
        String text = finalResult.toString();
        if (text.isEmpty()) {
            return "";
        }

        int start = 0;
        while (start < text.length() && text.charAt(start) < 33) {
            start++;
        }

        int end = text.length() - 1;
        while (end >= start && text.charAt(end) < 33) {
            end--;
        }

        if (start > end) {
            return "";
        }

        return text.substring(start, end + 1);
    }
}