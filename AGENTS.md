# Agent Instructions

## Source Lookup Rules

1. Prefer self-explanatory code, clear names, and small focused functions.
2. Avoid comments that merely restate what the code already says.
3. Add comments only when they explain non-obvious intent, constraints, or tradeoffs.
4. Wildcard imports such as `import package.*` are forbidden. Always use explicit imports.
5. Use CLI tools for reading project files and `rg` for searching project file names or contents.
6. Do not use IDEA MCP tools or the `workspace-agent-bridge` skill for project file lookup or content search.
7. When project source lookup cannot be satisfied locally, search source JARs under `~/.gradle/caches/`.

## Tool Preference For Other Work

For tasks other than searching or reading project files, prefer IDEA MCP tools and the `workspace-agent-bridge` skill when they provide the relevant operation, such as diagnostics, formatting, refactoring, or other IDE-aware actions.
