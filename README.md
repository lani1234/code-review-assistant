# AI-Powered Code Review Assistant

A Spring Boot CLI tool that uses Claude AI to perform comprehensive code reviews on Java files and projects.

## What It Does

This tool analyzes Java code files and provides detailed feedback on:
- **Code Quality** - Best practices, naming conventions, code organization
- **Potential Bugs** - Logic errors, null pointer risks, edge cases
- **Performance** - Inefficient algorithms, resource usage issues
- **Security** - Vulnerabilities, input validation, data exposure

## Example Output

Here's an actual code review of the `ClaudeService.java` file from this project:
```
File: ClaudeService.java
--------------------------------------------------------------------------------
CODE QUALITY Issues

HIGH Severity - Line 18
Issue: OkHttpClient instance should be injected as a Spring bean
Description: Current approach creates a new client for each service instance and 
doesn't allow for configuration externalization
Suggestion: Create an @Bean in a configuration class and inject it via constructor

MEDIUM Severity - Line 46-61
Issue: String formatting with String.format() and text blocks could be more maintainable
Description: Large prompt template embedded in code reduces maintainability
Suggestion: Move prompt template to external resource file or configuration

BUGS Issues

HIGH Severity - Line 96
Issue: Response body can only be read once - potential NullPointerException
Description: If response.body() is null, calling .string() will throw NPE
Suggestion: Add null check: response.body() != null ? response.body().string() : ""

MEDIUM Severity - Line 107-120
Issue: JSON parsing assumes specific response structure
Description: Code assumes content array exists and has at least one element with text field
Suggestion: Add defensive programming with proper null/empty checks

PERFORMANCE Issues

MEDIUM Severity - Line 18-21
Issue: OkHttpClient created per service instance
Description: OkHttpClient is expensive to create and should be shared
Suggestion: Use singleton pattern or Spring bean configuration

SECURITY Issues

HIGH Severity - Line 82
Issue: API key logged in error scenarios
Description: HTTP request headers containing API key might be logged in debug mode
Suggestion: Ensure sensitive headers are excluded from logging

MEDIUM Severity - Line 119
Issue: Error messages may expose internal structure
Description: Raw API responses logged on parsing failure could contain sensitive information
Suggestion: Sanitize logged response data, avoid logging full response content

Summary
Critical Issues to Address:
1. Fix response body null pointer risk
2. Move OkHttpClient to Spring bean configuration
3. Add proper input validation
4. Improve error handling consistency
5. Secure API key handling and logging
--------------------------------------------------------------------------------
```

The tool provides actionable feedback with:
- **Specific line numbers** for each issue
- **Severity levels** (HIGH, MEDIUM, LOW)
- **Clear descriptions** of what's wrong
- **Concrete suggestions** for fixes
- **Summary** of critical issues to prioritize

## Tech Stack

- **Java 21**
- **Spring Boot 3.2.2**
- **Maven**
- **Anthropic Claude API** (Claude Sonnet 4)
- **OkHttp** - HTTP client
- **Gson** - JSON processing
- **Lombok** - Boilerplate reduction

## Prerequisites

- Java 21 or later
- Maven 3.6+
- Anthropic API key

## Setup

### 1. Clone the Repository
```bash
git clone https://github.com/lani1234/code-review-assistant.git
cd code-review-assistant
```

### 2. Get Your API Key

1. Go to [console.anthropic.com](https://console.anthropic.com)
2. Sign up (get $5 in free credits)
3. Generate an API key
4. Copy the key

### 3. Set Your API Key
```bash
export ANTHROPIC_API_KEY="your-api-key-here"
```

To make it permanent, add to your `~/.zprofile`:
```bash
echo 'export ANTHROPIC_API_KEY="your-api-key-here"' >> ~/.zprofile
source ~/.zprofile
```

### 4. Build the Project
```bash
mvn clean install
```

## Usage

### Review a Single File
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--file src/main/java/MyClass.java"
```

### Review an Entire Directory
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--dir src/main/java"
```

Reviews all `.java` files recursively in the directory.

### Using the JAR Directly (Faster)
```bash
# Build once
mvn clean package

# Review a file
java -jar target/code-review-assistant-1.0.0.jar --file MyClass.java

# Review a directory
java -jar target/code-review-assistant-1.0.0.jar --dir src/main/java
```

### Create a Convenient Script

Create `review.sh`:
```bash
#!/bin/bash
export ANTHROPIC_API_KEY="your-key-here"
java -jar target/code-review-assistant-1.0.0.jar "$@"
```

Make it executable:
```bash
chmod +x review.sh

# Then use it easily:
./review.sh --file MyClass.java
./review.sh --dir src/main/java
```

## Configuration

Customize settings in `src/main/resources/application.yml`:
```yaml
review:
  file-extensions:
    - .java
  max-file-size: 100000  # Max characters per file
  aspects:
    - code-quality
    - bugs
    - performance
    - security
```

## Troubleshooting

### "ANTHROPIC_API_KEY not set"
Set your API key as an environment variable:
```bash
export ANTHROPIC_API_KEY="your-key"
```

### "File not found" or "Directory not found"
Use absolute paths or paths relative to where you run the command:
```bash
# Absolute path
mvn spring-boot:run -Dspring-boot.run.arguments="--file /full/path/to/File.java"

# Relative path (from project root)
mvn spring-boot:run -Dspring-boot.run.arguments="--file src/main/java/File.java"
```

### "API call failed with status 529"
The API is temporarily overloaded. Wait 1-2 minutes and try again.

### "timeout" errors
Increase timeouts in `ClaudeService.java` if reviewing very large files:
```java
private final OkHttpClient httpClient = new OkHttpClient.Builder()
    .readTimeout(120, TimeUnit.SECONDS)  // Increase from 60 to 120
    .build();
```

### Java Version Issues
This project requires Java 21:
```bash
java -version
# Should show: openjdk version "21.x.x"

# Set Java 21
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home
```

## Limitations

- **Java files only** - Currently only reviews `.java` files
- **File size limit** - Files over 100KB are truncated (configurable)
- **No IDE integration** - Command-line only (IDE plugins possible future enhancement)
- **Sequential processing** - Reviews files one at a time

## Future Enhancements

- [ ] Generate markdown report files
- [ ] Support for other languages (Python, JavaScript, etc.)
- [ ] IDE plugins (IntelliJ, VS Code)
- [ ] Batch processing with parallel reviews
- [ ] Custom rule sets and severity thresholds
- [ ] Integration with CI/CD pipelines
- [ ] Diff-only mode (review only changed lines)
- [ ] Fix suggestions with code snippets
- [ ] Summary dashboard with metrics

