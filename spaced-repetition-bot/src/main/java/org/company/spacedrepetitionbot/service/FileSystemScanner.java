package org.company.spacedrepetitionbot.service;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class FileSystemScanner {
    public List<Path> findMarkdownFiles(Path directory) throws IOException {
        try (Stream<Path> pathStream = Files.walk(directory)) {
            return pathStream.filter(Files::isRegularFile)
                    .filter(this::isMarkdownFile)
                    .collect(Collectors.toList());
        }
    }

    private boolean isMarkdownFile(Path path) {
        return path.toString()
                .toLowerCase()
                .endsWith(".md");
    }
}
