package io.github.lani1234.codereview.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewIssue {
    private String category;  // BUGS, CODE_QUALITY, PERFORMANCE, SECURITY
    private String severity;  // HIGH, MEDIUM, LOW
    private int lineNumber;
    private String description;
}