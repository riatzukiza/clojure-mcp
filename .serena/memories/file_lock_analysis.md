## File Lock System Analysis

### Current Implementation

The ClojureMCP codebase does NOT currently implement a file lock plugin. Instead, it uses a **file timestamp tracking system** to prevent concurrent modifications:

**File Timestamp Tracking (`file_timestamps.clj`):**
- Tracks when files were last read via timestamps
- Checks if files have been modified since last read before allowing edits
- Provides safety but NOT locking
- Configured via `:write-file-guard` setting (can be disabled)

### Write Operations That Should Be Locked

Based on my analysis, the following are WRITE operations that should have short-lived locks:

1. **File Write Operations** (`file_write/core.clj`):
   - `write-clojure-file` - writes formatted Clojure files
   - `write-text-file` - writes plain text files

2. **File Edit Operations** (`file_edit/core.clj`):
   - `perform-file-edit` - replaces text within files
   - `save-file-content` - saves edited content

3. **Form Edit Operations** (`form_edit/core.clj`):
   - `replace-top-level-form` - replaces entire Clojure forms
   - `edit-top-level-form` - inserts before/after forms
   - Core form manipulation functions

4. **Comment Edit Operations** (`form_edit/pipeline.clj`):
   - `edit-comment-block` - modifies comment blocks
   - Comment form and line comment editing

### Read Operations That Should NOT Be Locked

1. **File Read Operations** (`unified_read_file/tool.clj`):
   - `read-file-with-timestamp` - reads files and updates timestamp
   - Should remain completely unlocked for concurrent reads

2. **Content Exploration** (`grep`, `glob_files`, `directory_tree`):
   - Search and exploration tools
   - Must support concurrent access

### Required: New File Lock Plugin

A proper file lock system should implement:

**Short-Lived Write-Only Locking Pattern:**
1. **Check for lock** before any write operation
2. **Create lock** if none exists  
3. **Perform write operation**
4. **Release lock** immediately after write completes
5. **Never lock reads** - reads should proceed without restriction

**Integration Points:**
- Replace `file-modified-since-read?` checks in write pipelines
- Add lock management to `file_write/core.clj`, `file_edit/core.clj`, `form_edit/pipeline.clj`
- Keep timestamp tracking as additional safety layer