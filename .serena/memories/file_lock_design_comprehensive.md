## Comprehensive File Lock Design

### Universal File Lock Plugin Requirements

**Core Principle**: File locking should be **file-type agnostic** - ALL write operations should be protected, regardless of file extension or content type.

### Locks for ALL File Types

The file lock system should protect writes for:

1. **Clojure Files** (`*.clj`, `*.cljs`, `*.cljc`, `*.edn`, `*.bb`)
2. **Configuration Files** (`*.edn`, `*.json`, `*.yaml`, `*.toml`, `*.xml`)  
3. **Documentation Files** (`*.md`, `*.txt`, `*.rst`, `*.adoc`)
4. **Data Files** (`*.csv`, `*.tsv`, `*.jsonl`, `*.log`, `*.txt`)
5. **Script Files** (`*.sh`, `*.py`, `*.js`, `*.rb`, `*.go`, etc.)
6. **Binary Files** (`*.jpg`, `*.png`, `*.pdf`, `*.zip`, etc.)
7. **Any Custom File Types** - User-defined extensions via configuration

### Universal Lock Implementation Strategy

#### File-Type Agnostic Approach:
```clojure
;; Determine if file should have write protection based on configuration
(defn should-lock-file? [nrepl-client-atom file-path config]
  (let [file-ext (get-file-extension file-path)
        protected-extensions (or (:write-protected-extensions config)
                             default-protected-extensions))]
    (some #(str/ends-with? file-path %) protected-extensions)))
```

#### Integration Points:
- **file_write/core.clj** - All file writes
- **file_edit/core.clj** - All text file edits  
- **form_edit/pipeline.clj** - All structured file edits
- **Any future write tools** - Universal protection

#### Configuration Options:
```clojure
{:write-protected-extensions [".clj" ".cljs" ".cljc" ".edn" ".bb"
                        ".json" ".yaml" ".toml" ".xml" 
                        ".md" ".txt" ".rst" ".adoc"
                        ".csv" ".tsv" ".log" ".py"
                        ".js" ".sh" ".go" ".rb"]
 :lock-timeout-minutes 5
 :lock-cleanup-interval 10}
```

### Benefits Over Current System:

1. **Type Agnostic**: Works for any file type, not just Clojure
2. **Configurable**: Users can specify which extensions to protect
3. **Future-Proof**: Extensible to new file types and tools
4. **Comprehensive**: Protects ALL write operations universally

The current file-type-specific approach is too limited and won't work for configuration files, documentation, or other file types that the MCP system needs to handle.