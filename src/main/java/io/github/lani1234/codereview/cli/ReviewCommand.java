package io.github.lani1234.codereview.cli;

import io.github.lani1234.codereview.model.CodeReview;
import io.github.lani1234.codereview.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewCommand implements CommandLineRunner {

    private final ReviewService reviewService;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== Code Review Assistant ===\n");

        if (args.length == 0) {
            printUsage();
            return;
        }

        try {
            String mode = args[0];

            if ("--file".equals(mode) && args.length >= 2) {
                reviewSingleFile(args[1]);
            } else if ("--dir".equals(mode) && args.length >= 2) {
                reviewDirectory(args[1]);
            } else {
                printUsage();
            }

        } catch (Exception e) {
            log.error("Error during code review", e);
            System.err.println("\nError: " + e.getMessage());
            System.err.println("\nPossible causes:");
            System.err.println("  - API key not set or invalid");
            System.err.println("  - File or directory not found");
            System.err.println("  - Network connectivity issues");
        }
    }

    private void reviewSingleFile(String filepath) throws Exception {
        System.out.println("Reviewing file: " + filepath + "\n");

        CodeReview review = reviewService.reviewFile(filepath);

        printReview(review);
    }

    private void reviewDirectory(String dirPath) throws Exception {
        System.out.println("Reviewing directory: " + dirPath + "\n");

        List<CodeReview> reviews = reviewService.reviewDirectory(dirPath);

        System.out.println("Reviewed " + reviews.size() + " files\n");
        System.out.println("=".repeat(80) + "\n");

        for (CodeReview review : reviews) {
            printReview(review);
            System.out.println("\n" + "=".repeat(80) + "\n");
        }
    }

    private void printReview(CodeReview review) {
        System.out.println("File: " + review.getFilename());
        System.out.println("-".repeat(80));
        System.out.println(review.getReviewText());
        System.out.println("-".repeat(80));
    }

    private void printUsage() {
        System.out.println("Usage:");
        System.out.println("  Review a single file:");
        System.out.println("    mvn spring-boot:run -Dspring-boot.run.arguments=\"--file path/to/File.java\"");
        System.out.println();
        System.out.println("  Review a directory:");
        System.out.println("    mvn spring-boot:run -Dspring-boot.run.arguments=\"--dir path/to/src\"");
        System.out.println();
        System.out.println("Make sure to set your API key:");
        System.out.println("  export ANTHROPIC_API_KEY=\"your-key-here\"");
    }
}