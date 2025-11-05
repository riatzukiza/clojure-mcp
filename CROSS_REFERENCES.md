# Clojure MCP Cross-Reference Documentation

> REPL-driven development with AI assistance for Clojure and ClojureScript with comprehensive repository cross-references

## 🔗 Repository Cross-References

This document provides comprehensive cross-references to all related repositories in the Clojure and AI development ecosystem, enabling agents to navigate between related tools, protocols, and integration patterns seamlessly.

### 🏗️ Development Infrastructure Dependencies

#### **Agent Development & Orchestration**
- **[promethean](https://github.com/riatzukiza/promethean)** - Local LLM enhancement system and autonomous agent framework
  - [AGENTS.md](https://github.com/riatzukiza/promethean/blob/main/AGENTS.md)
  - [CROSS_REFERENCES.md](https://github.com/riatzukiza/promethean/blob/main/CROSS_REFERENCES.md)
  - [README.md](https://github.com/riatzukiza/promethean/blob/main/README.md)
  - **Integration**: Use Promethean agents for enhanced Clojure code analysis and autonomous development

#### **Agent Shell Integration**
- **[agent-shell](https://github.com/riatzukiza/agent-shell)** - Emacs-based agent shell for ACP (Agent Client Protocol)
  - [AGENTS.md](https://github.com/riatzukiza/agent-shell/blob/main/AGENTS.md)
  - [CROSS_REFERENCES.md](https://github.com/riatzukiza/agent-shell/blob/main/CROSS_REFERENCES.md)
  - [README.md](https://github.com/riatzukiza/agent-shell/blob/main/README.org)
  - **Integration**: Protocol reference for MCP implementation and Emacs integration

### 🔧 Authentication & Security Dependencies

#### **OAuth Authentication Patterns**
- **[opencode-openai-codex-auth](https://github.com/numman-ali/opencode-openai-codex-auth)** - OpenAI Codex OAuth authentication plugin
  - [AGENTS.md](https://github.com/numman-ali/opencode-openai-codex-auth/blob/main/AGENTS.md)
  - [README.md](https://github.com/numman-ali/opencode-openai-codex-auth/blob/main/README.md)
  - **Integration**: Authentication patterns for cloud LLM integration with Clojure MCP

#### **TypeScript SDK Integration**
- **[moofone/codex-ts-sdk](https://github.com/moofone/codex-ts-sdk)** - TypeScript SDK for OpenAI Codex with cloud tasks
  - [AGENTS.md](https://github.com/moofone/codex-ts-sdk/blob/main/AGENTS.md)
  - [README.md](https://github.com/moofone/codex-ts-sdk/blob/main/README.md)
  - **Integration**: Cross-language SDK patterns for Clojure-TypeScript interoperability

### 🌐 Web & Frontend Integration

#### **OpenCode Development**
- **[stt](https://github.com/riatzukiza/devel/tree/main/stt)** - Multiple opencode development branches and experiments
  - [AGENTS.md](https://github.com/riatzukiza/devel/blob/main/stt/AGENTS.md)
  - [CROSS_REFERENCES.md](https://github.com/riatzukiza/devel/blob/main/stt/CROSS_REFERENCES.md)
  - **Integration**: Clojure syntax highlighting and REPL integration for OpenCode

- **[opencode-hub](https://github.com/riatzukiza/devel/tree/main/opencode-hub)** - Centralized opencode coordination and distribution
  - [AGENTS.md](https://github.com/riatzukiza/devel/blob/main/opencode-hub/AGENTS.md)
  - [README.md](https://github.com/riatzukiza/devel/blob/main/opencode-hub/README.md)
  - **Integration**: Package distribution for Clojure MCP tools

#### **Full-Stack Applications**
- **[riatzukiza/openhax](https://github.com/riatzukiza/openhax)** - Full-stack application with Reactant + Fastify
  - [AGENTS.md](https://github.com/riatzukiza/openhax/blob/main/AGENTS.md)
  - **Integration**: Full-stack Clojure/TypeScript development patterns

### ⚙️ Configuration & Environment

#### **System Configuration**
- **[dotfiles](https://github.com/riatzukiza/devel/tree/main/dotfiles)** - System configuration and environment setup
  - [AGENTS.md](https://github.com/riatzukiza/devel/blob/main/dotfiles/.config/opencode/AGENTS.md)
  - **Integration**: Clojure development environment setup and tooling configuration

### 🔌 Runtime & Performance

#### **Rust-Based Runtime**
- **[openai/codex](https://github.com/openai/codex)** - Rust-based Codex CLI and runtime
  - [AGENTS.md](https://github.com/openai/codex/blob/main/AGENTS.md)
  - [README.md](https://github.com/openai/codex/blob/main/README.md)
  - **Integration**: High-performance runtime components for Clojure MCP

## 🔄 Clojure MCP Integration Patterns

### **REPL-Driven Development Integration**
#### **Agent Enhancement**
- **Promethean Integration**: Use [promethean](https://github.com/riatzukiza/promethean) agents for advanced code analysis
- **Cloud LLM Enhancement**: Combine local REPL with cloud AI capabilities
- **Autonomous Development**: Enable self-improving Clojure code with agent assistance

#### **Enhanced REPL Workflow**
```bash
# Start enhanced Clojure MCP with Promethean agents
cd ../promethean && pnpm --filter @promethean-os/agent start
clojure -X:mcp :port 7888 :agent-enhancement true

# Agent-assisted REPL development
clojure -M:nrepl
# Connect MCP server with agent tools enabled
```

### **Protocol Development Integration**
#### **MCP Protocol Reference**
- **Agent Shell Reference**: Use [agent-shell](https://github.com/riatzukiza/agent-shell) ACP implementation as reference
- **Protocol Patterns**: Cross-protocol implementation patterns
- **Standardization**: Ensure compatibility with other agent protocols

#### **Protocol Development**
```bash
# Protocol reference study
cd ../agent-shell && emacs agent-shell.el
# Study ACP implementation patterns

# MCP protocol enhancement
clojure -X:mcp :protocol-enhancement true
# Implement cross-protocol compatibility
```

### **Authentication Integration**
#### **Cloud LLM Authentication**
- **OAuth Patterns**: Use [opencode-openai-codex-auth](https://github.com/numman-ali/opencode-openai-codex-auth) patterns
- **Cross-Language SDK**: Integrate with [moofone/codex-ts-sdk](https://github.com/moofone/codex-ts-sdk) for TypeScript compatibility
- **Secure Integration**: Ensure secure cloud LLM access from Clojure MCP

#### **Authentication Development**
```bash
# OAuth pattern integration
cd ../opencode-openai-codex-auth
pnpm build && pnpm test

# TypeScript SDK integration
cd ../moofone/codex-ts-sdk
pnpm build

# Clojure MCP authentication
clojure -X:mcp :oauth-config "../opencode-openai-codex-auth/config.json"
```

### **Web Integration**
#### **OpenCode Clojure Support**
- **Syntax Highlighting**: Integrate with [stt/opencode-feat-clojure-syntax-highlighting](https://github.com/riatzukiza/devel/tree/main/stt/opencode-feat-clojure-syntax-highlighting)
- **REPL Integration**: Provide REPL capabilities to OpenCode web interface
- **Tool Distribution**: Package through [opencode-hub](https://github.com/riatzukiza/devel/tree/main/opencode-hub)

#### **Web Development Workflow**
```bash
# OpenCode Clojure integration
cd ../stt/opencode-feat-clojure-syntax-highlighting
bun dev  # with Clojure MCP backend

# Package distribution
cd ../opencode-hub
pnpm publish clojure-mcp-tools
```

### **Full-Stack Integration**
#### **Clojure-TypeScript Interop**
- **Cross-Language Development**: Use [riatzukiza/openhax](https://github.com/riatzukiza/openhax) patterns
- **Shared Tooling**: Develop tools that work across Clojure and TypeScript
- **Unified Development**: Seamless development experience across languages

#### **Full-Stack Development**
```bash
# Full-stack Clojure-TypeScript development
cd ../riatzukiza/openhax
pnpm install

# Cross-language tooling
clojure -X:mcp :typescript-integration true
# Enable TypeScript-aware Clojure tools
```

## 🔄 Cross-Repository Development Workflows

### **Enhanced REPL Development Workflow**
1. **Base Setup**: Start Clojure MCP with nREPL
2. **Agent Integration**: Connect [promethean](https://github.com/riatzukiza/promethean) agents for enhancement
3. **Authentication**: Configure cloud LLM access via [opencode-openai-codex-auth](https://github.com/numman-ali/opencode-openai-codex-auth)
4. **Development**: Interactive REPL development with AI assistance
5. **Validation**: Built-in linting and formatting

### **Protocol Development Workflow**
1. **Reference Study**: Analyze [agent-shell](https://github.com/riatzukiza/agent-shell) ACP implementation
2. **MCP Enhancement**: Implement cross-protocol compatibility
3. **Testing**: Test with multiple agent implementations
4. **Documentation**: Update protocol documentation

### **Web Integration Workflow**
1. **OpenCode Integration**: Develop with [stt](https://github.com/riatzukiza/devel/tree/main/stt) branches
2. **Syntax Highlighting**: Clojure language support in web interfaces
3. **REPL Web Interface**: Provide REPL capabilities through web
4. **Distribution**: Package through [opencode-hub](https://github.com/riatzukiza/devel/tree/main/opencode-hub)

## 📋 Quick Reference Commands

### **Cross-Repository Development**
```bash
# Full Clojure AI development environment
cd ../promethean && pnpm build
cd ../agent-shell && make compile
cd ../opencode-openai-codex-auth && pnpm build

# Enhanced Clojure MCP
clojure -X:mcp :port 7888 :agent-enhancement true :oauth-config "../auth/config.json"
```

### **Integration Testing**
```bash
# Agent integration testing
cd ../promethean && pnpm --filter @promethean-os/agent test --clojure-mcp

# Protocol compatibility testing
cd ../agent-shell && make test
clojure -X:test :protocol-compatibility true

# Authentication testing
cd ../opencode-openai-codex-auth && pnpm test
clojure -X:test :oauth-integration true
```

### **Web Development Testing**
```bash
# OpenCode integration testing
cd ../stt/opencode-feat-clojure-syntax-highlighting
bun dev  # with Clojure MCP backend

# Full-stack testing
cd ../riatzukiza/openhax
pnpm test --clojure-integration
```

## 🎯 Decision Trees for Agents

### **Choosing Integration Pattern**
- **Enhanced REPL development?** → [promethean](https://github.com/riatzukiza/promethean) + [opencode-openai-codex-auth](https://github.com/numman-ali/opencode-openai-codex-auth)
- **Protocol development?** → [agent-shell](https://github.com/riatzukiza/agent-shell) for reference + cross-protocol testing
- **Web integration?** → [stt](https://github.com/riatzukiza/devel/tree/main/stt) + [opencode-hub](https://github.com/riatzukiza/devel/tree/main/opencode-hub)
- **Full-stack development?** → [riatzukiza/openhax](https://github.com/riatzukiza/openhax) + [moofone/codex-ts-sdk](https://github.com/moofone/codex-ts-sdk)

### **Integration Complexity**
- **Simple**: Clojure MCP + basic agent integration
- **Medium**: Clojure MCP + authentication + web integration
- **Complex**: Full ecosystem integration with all repositories

## 📚 Additional Documentation

- **[Workspace Documentation](https://github.com/riatzukiza/devel/blob/main/AGENTS.md)** - Main workspace coordination
- **[Repository Index](https://github.com/riatzukiza/devel/blob/main/REPOSITORY_INDEX.md)** - Complete repository overview
- **[Git Submodules Documentation](https://github.com/riatzukiza/devel/blob/main/docs/reports/research/git-submodules-documentation.md)** - Technical submodule details
- **[Promethean Cross-References](https://github.com/riatzukiza/promethean/blob/main/CROSS_REFERENCES.md)** - Agent framework integration
- **[Agent Shell Cross-References](https://github.com/riatzukiza/agent-shell/blob/main/CROSS_REFERENCES.md)** - Protocol integration

## 🌐 External Resources

- **[Main Documentation](https://github.com/bhauman/clojure-mcp/blob/main/README.md)** - Official Clojure MCP documentation
- **[Configuration Guide](https://github.com/bhauman/clojure-mcp/blob/main/doc/CONFIG.md)** - Configuration reference
- **[MCP Specification](https://modelcontextprotocol.io/)** - Model Context Protocol specification
- **[Clojurians Slack](https://clojurians.slack.com/)** - #ai-assisted-coding channel

---

## License

GNU Affero General Public License v3.0