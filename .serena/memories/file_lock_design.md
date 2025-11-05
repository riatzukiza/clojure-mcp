## File Lock Plugin Design

### Architecture

**Core Requirements:**
1. Only lock on write operations (never on reads)
2. Short-lived locks (duration of the write operation only)
3. Check-create-write-unlock pattern
4. Thread-safe implementation
5. Integration with existing timestamp tracking

### Implementation Plan

**New Namespace: `clojure-mcp.tools.file-locks`**

#### Core Functions:
1. `acquire-write-lock!` - Attempts to acquire a lock for a file
2. `release-write-lock!` - Releases a lock
3. `with-write-lock` - Macro for safe locked operations
4. `file-locked-for-write?` - Check if file is locked

#### Lock Strategy:
- Use file-based locks in `.clojure-mcp/locks/` directory
- Lock files named after target file with `.lock` extension
- Lock files contain process ID and timestamp
- Automatic cleanup of stale locks (older than X minutes)

#### Integration Points:
- Replace `file-modified-since-read?` checks in write pipelines
- Add lock management to all write operations
- Maintain backward compatibility with timestamp system

### Configuration Options:
- `:enable-file-locks` - Enable/disable the system
- `:lock-timeout-minutes` - How long before considering locks stale
- `:lock-cleanup-interval` - How often to clean stale locks