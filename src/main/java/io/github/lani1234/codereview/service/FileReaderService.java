package io.github.lani1234.codereview.service;

import io.github.lani1234.codereview.config.ReviewConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileReaderService {

    private final ReviewConfig config;

    /**
     * Read a single file
     */
    public String readFile(String filepath) throws IOException {
        Path path = Paths.get(filepath);

        if (!Files.exists(path)) {
            throw new IOException("File not found: " + filepath);
        }

        if (!Files.isRegularFile(path)) {
            throw new IOException("Not a file: " + filepath);
        }

        if (!isJavaFile(filepath)) {
            throw new IOException("Not a Java file: " + filepath);
        }

        String content = Files.readString(path);

        if (content.length() > config.getMaxFileSize()) {
            log.warn("File {} is large ({} chars), truncating to {}",
                    filepath, content.length(), config.getMaxFileSize());
            content = content.substring(0, config.getMaxFileSize());
        }

        return content;
    }

    /**
     * Read all Java files from a directory (recursive)
     */
    public List<Path> findJavaFiles(String dirPath) throws IOException {
        Path dir = Paths.get(dirPath);

        if (!Files.exists(dir)) {
            throw new IOException("Directory not found: " + dirPath);
        }

        if (!Files.isDirectory(dir)) {
            throw new IOException("Not a directory: " + dirPath);
        }

        try (Stream<Path> paths = Files.walk(dir)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(p -> isJavaFile(p.toString()))
                    .collect(Collectors.toList());
        }
    }

    /**
     * Check if file is a Java file
     */
    private boolean isJavaFile(String filepath) {
        return config.getFileExtensions().stream()
                .anyMatch(ext -> filepath.endsWith(ext));
    }

    /**
     * Get relative path for display
     */
    public String getRelativePath(Path file, String baseDir) {
        Path base = Paths.get(baseDir);
        return base.relativize(file).toString();
    }
}