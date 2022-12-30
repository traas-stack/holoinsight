# Pre Commit Guide
这个文档介绍如何在开发环境使用 [pre-commit](https://pre-commit.com/).
我们使用 pre-commit 来实现:
1. Java 代码格式化
2. Java 代码添加License Header
3. 验证 commit message 满足 [commitizen](https://github.com/commitizen-tools/commitizen) 规范

# Quick start
## 1. 安装 pre-commit
首先, 你需要安装 pre-commit. 参考 [Installation](https://pre-commit.com/#installation).

## 2. 安装 git hooks (钩子)
```bash
pre-commit install -t pre-commit -t commit-msg -t pre-push
```

输出示例:
```text
pre-commit installed at .git/hooks/pre-commit
pre-commit installed at .git/hooks/commit-msg
pre-commit installed at .git/hooks/pre-push
```

## 3. (可选) 手动执行 pre-commit
作用于本次变更的文件:
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

作用于所有文件:
```bash
pre-commit run --all-files
```

更多细节参考 pre-commit 官方文档.

## 4. 提交代码时触发 pre-commit
```bash
git commit -m "..."
```
