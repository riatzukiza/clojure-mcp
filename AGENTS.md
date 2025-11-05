# Clojure MCP

> REPL-driven development with AI assistance for Clojure and ClojureScript

## Overview

Clojure MCP provides an MCP (Model Context Protocol) server that connects AI models to Clojure nREPL environments, enabling remarkable REPL-driven development experiences with specialized Clojure tooling.

## Key Features

- **REPL Integration**: Direct nREPL connection with syntax-aware evaluation
- **Clojure Tooling**: Built-in linting, formatting, and paren balancing
- **Structure-Aware Editing**: Form-based operations that prevent syntax errors
- **Shadow-CLJS Support**: Full ClojureScript development support
- **Agent Tools**: Autonomous code exploration and analysis capabilities

## Build & Development Commands

```bash
# Run tests
clojure -X:test

# Run linter
clojure -M:lint

# Start MCP server
clojure -X:mcp :port 7888

# Start with automatic nREPL
clojure -X:mcp :start-nrepl-cmd '["clojure" "-M:nrepl"]'

# Index codebase for agents
clojure -X:index
```

## Code Style

- **REPL-First**: Prefer interactive development in the REPL
- **Small Steps**: Make incremental changes with validation
- **Form-Based**: Use structure-aware editing over text manipulation
- **Syntax Validation**: Always lint and format after edits
- **Human in Loop**: Maintain developer oversight and guidance

## MCP Server Configuration

### Basic Setup
```clojure
{:allowed-directories ["."
                       "src"
                       "test"
                       "resources"]
 :write-file-guard :full-read
 :cljfmt true
 :bash-over-nrepl true
 :scratch-pad-load false}
```

### Advanced Configuration
```clojure
{:allowed-directories ["."
                       "../shared-utils"
                       "/absolute/path/to/code"]
 :scratch-pad-load true
 :scratch-pad-file "workspace.edn"
 :cljfmt false
 :bash-over-nrepl false}
```

## Cross-Repository Integration

### Related Tools
- **[promethean](../promethean/)**: Agent orchestration and enhancement patterns
- **[agent-shell](../agent-shell/)**: ACP protocol implementation reference
- **[opencode-openai-codex-auth](../opencode-openai-codex-auth/)**: Authentication and plugin patterns

### 🔗 Comprehensive Cross-References
- **[CROSS_REFERENCES.md](./CROSS_REFERENCES.md)** - Complete cross-references to all related repositories
- **[Workspace AGENTS.md](../AGENTS.md)** - Main workspace documentation
- **[Repository Index](../REPOSITORY_INDEX.md)** - Complete repository overview

### Integration Patterns
1. **Agent Enhancement**: Use with promethean for cloud LLM enhancement
2. **Protocol Development**: MCP server implementation patterns for other languages
3. **REPL Patterns**: Structure-aware editing patterns for other development tools
4. **Authentication**: Combine with opencode-openai-codex-auth for OAuth flows

## Development Workflow

1. **REPL Development**: Start nREPL and connect MCP server
2. **Interactive Coding**: Use `clojure_eval` for rapid iteration
3. **Structure Editing**: Use `clojure_edit` for safe modifications
4. **Validation**: Built-in linting prevents syntax errors
5. **File Operations**: Use form-aware tools for code changes

## Tool Categories

### Read-Only Tools
- `LS`: Recursive file tree exploration
- `read_file`: Smart file reading with pattern matching
- `grep`: Fast content search with regex
- `glob_files`: Pattern-based file finding

### Code Evaluation
- `clojure_eval`: nREPL evaluation with helper functions
- `bash`: Shell command execution (local or nREPL)

### File Editing
- `clojure_edit`: Structure-aware Clojure form editing
- `clojure_edit_replace_sexp`: Expression-level modifications
- `file_edit`: Simple text replacement
- `file_write`: Complete file writing with validation

### Agent Tools
- `dispatch_agent`: Autonomous multi-step exploration
- `architect`: Technical planning and system design
- `code_critique`: Interactive code review

## Resources

- [Main Documentation](https://github.com/bhauman/clojure-mcp/blob/main/README.md)
- [Configuration Guide](https://github.com/bhauman/clojure-mcp/blob/main/doc/CONFIG.md)
- [Customization Guide](https://github.com/bhauman/clojure-mcp/blob/main/doc/custom-mcp-server.md)
- [MCP Specification](https://modelcontextprotocol.io/)

## Dependencies

- **nREPL**: Clojure network REPL server
- **clj-kondo**: Clojure linter for syntax validation
- **cljfmt**: Clojure code formatter
- **Java 17+**: Required runtime environment

## License

GNU Affero General Public License v3.0

## Community

- **Clojurians Slack**: #ai-assisted-coding channel
- **GitHub Issues**: [Report bugs and request features](https://github.com/bhauman/clojure-mcp/issues)
- **Wiki**: [Community documentation](https://github.com/bhauman/clojure-mcp/wiki)