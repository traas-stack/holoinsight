# Pre Commit Guide
This document describes how we use [pre-commit](https://pre-commit.com/) in our development.
We use pre-commit to:
1. Format Java source codes
2. Add license header for Java source codes
3. Validates that a commit message matches the [commitizen](https://github.com/commitizen-tools/commitizen) schema

# Quick start
## 1. Install pre-commit
First, you need to have the pre-commit package manager installed. see [Installation](https://pre-commit.com/#installation).

## 2. Install git hooks
```bash
pre-commit install -t pre-commit -t commit-msg -t pre-push
```

example output:
```text
pre-commit installed at .git/hooks/pre-commit
pre-commit installed at .git/hooks/commit-msg
pre-commit installed at .git/hooks/pre-push
```

## 3. (Optional) Run pre-commit manually
Apply to current staging files.
```bash
pre-commit
```

```text
check yaml...............................................................Passed
check json...........................................(no files to check)Skipped
don't commit to branch...................................................Passed
check for added large files..............................................Passed
check for case conflicts.................................................Passed
add license for java.................................(no files to check)Skipped
```

Apply to all files.
```bash
pre-commit run --all-files
```

For more details, refer to pre-commit official documentation.

## 4. Trigger pre-commit when committing
```bash
git commit -m "..."
```
