package io.github.lani1234.codereview.service;

import io.github.lani1234.codereview.model.CodeReview;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final FileReaderService fileReaderService;
    private final ClaudeService claudeService;

    /**
     * Review a single file
     */
    public CodeReview reviewFile(String filepath) throws IOException {
        log.info("Starting review of file: {}", filepath);

        String code = fileReaderService.readFile(filepath);
        String filename = Path.of(filepath).getFileName().toString();

        String reviewText = claudeService.reviewCode(filename, code);

        CodeReview review = new CodeReview();
        review.setFilename(filename);
        review.setReviewText(reviewText);

        return review;
    }

    /**
     * Review all files in a directory
     */
    public List<CodeReview> reviewDirectory(String dirPath) throws IOException {
        log.info("Starting review of directory: {}", dirPath);

        List<Path> javaFiles = fileReaderService.findJavaFiles(dirPath);

        if (javaFiles.isEmpty()) {
            throw new IOException("No Java files found in directory: " + dirPath);
        }

        log.info("Found {} Java files to review", javaFiles.size());

        List<CodeReview> reviews = new ArrayList<>();

        for (Path file : javaFiles) {
            try {
                String relativePath = fileReaderService.getRelativePath(file, dirPath);
                String code = fileReaderService.readFile(file.toString());

                String reviewText = claudeService.reviewCode(relativePath, code);

                CodeReview review = new CodeReview();
                review.setFilename(relativePath);
                review.setReviewText(reviewText);
                reviews.add(review);

                // Small delay to avoid rate limits
                Thread.sleep(1000);

            } catch (Exception e) {
                log.error("Failed to review file: {}", file, e);
                // Continue with other files
            }
        }

        return reviews;
    }
}