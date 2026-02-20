# AI-Powered Code Review Assistant

A Spring Boot CLI tool that uses Claude AI to perform comprehensive code reviews on Java files and projects.

## What It Does

This tool analyzes Java code files and provides detailed feedback on:
- **Code Quality** - Best practices, naming conventions, code organization
- **Potential Bugs** - Logic errors, null pointer risks, edge cases
- **Performance** - Inefficient algorithms, resource usage issues
- **Security** - Vulnerabilities, input validation, data exposure

## Example Output
```
File: UserService.java
--------------------------------------------------------------------------------
CODE QUALITY Issues

MEDIUM Severity - Line 23
Issue: Method too long (85 lines)
Description: Consider breaking into smaller, focused methods
Suggestion: Extract validation logic into separate method

BUGS Issues

HIGH Severity - Line 45
Issue: Potential NullPointerException
Description: userService.getUser() may return null
Suggestion: Add null check before accessing user properties
--------------------------------------------------------------------------------
```

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

