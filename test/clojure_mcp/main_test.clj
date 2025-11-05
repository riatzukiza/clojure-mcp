(ns clojure-mcp.main-test
  (:require [clojure.test :refer :all]
            [clojure-mcp.main :as main]
            [clojure-mcp.tools :as tools]
            [clojure-mcp.prompts :as prompts]
            [clojure-mcp.resources :as resources]
            [clojure-mcp.nrepl :as nrepl]))

(deftest test-make-resources
  (testing "Resource creation factory function"
    (let [nrepl-client-atom (atom {:test "client"})
          working-dir "/tmp/test"
          resources-result (main/make-resources nrepl-client-atom working-dir)]
      
      (testing "returns vector of resources"
        (is (vector? resources-result))
        (is (pos? (count resources-result)))
      
      (testing "resources have required MCP structure"
        (doseq [resource resources-result]
          (is (map? resource))
          (is (contains? resource :uri))
          (is (contains? resource :name))
          (is (contains? resource :description))
          (is (string? (:uri resource)))
          (is (string? (:name resource)))
          (is (string? (:description resource)))))
      
      (testing "resources are created with nREPL client"
        ;; Verify that resources can access nREPL functionality
        (is (some? (some #(when (:nrepl-client %) %) resources-result)))))))

(deftest test-make-prompts
  (testing "Prompt creation factory function"
    (let [nrepl-client-atom (atom {:test "client"})
          working-dir "/tmp/test"
          prompts-result (main/make-prompts nrepl-client-atom working-dir)]
      
      (testing "returns vector of prompts"
        (is (vector? prompts-result))
        (is (pos? (count prompts-result)))
      
      (testing "prompts have required MCP structure"
        (doseq [prompt prompts-result]
          (is (map? prompt))
          (is (contains? prompt :name))
          (is (contains? prompt :description))
          (is (contains? prompt :arguments))
          (is (string? (:name prompt)))
          (is (string? (:description prompt)))
          (is (vector? (:arguments prompt))))
      
      (testing "prompts are created with nREPL client"
        ;; Verify that prompts can access nREPL functionality
        (is (some? (some #(when (:nrepl-client %) %) prompts-result)))))))

(deftest test-make-tools
  (testing "Tool creation factory function"
    (let [nrepl-client-atom (atom {:test "client"})
          working-directory "/tmp/test"
          tools-result (main/make-tools nrepl-client-atom working-directory)]
      
      (testing "returns vector of tools"
        (is (vector? tools-result))
        (is (pos? (count tools-result)))
      
      (testing "tools have required MCP structure"
        (doseq [tool tools-result]
          (is (map? tool))
          (is (contains? tool :name))
          (is (contains? tool :description))
          (is (or (contains? tool :inputSchema)
                   (contains? tool :input_schema)))
          (is (string? (:name tool)))
          (is (string? (:description tool))))
      
      (testing "tools are created with nREPL client"
        ;; Verify that tools can access nREPL functionality
        (is (some? (some #(when (:nrepl-client %) %) tools-result)))))))

(deftest test-deprecated-functions
  (testing "Deprecated functions for backward compatibility"
    (let [nrepl-client-atom (atom {:test "client"})
          working-dir "/tmp/test"]
      
      (testing "my-prompts deprecated function works"
        (let [prompts-result (main/my-prompts working-dir nrepl-client-atom)]
          (is (vector? prompts-result))
          (is (pos? (count prompts-result))))
      
      (testing "my-resources deprecated function works"
        (let [resources-result (main/my-resources nrepl-client-atom working-dir)]
          (is (vector? resources-result))
          (is (pos? (count resources-result))))
      
      (testing "my-tools deprecated function works"
        (let [tools-result (main/my-tools nrepl-client-atom)]
          (is (vector? tools-result))
          (is (pos? (count tools-result)))))))

(deftest test-start-mcp-server
  (testing "Main MCP server startup function"
    (let [opts {:port 7888}]
      
      (testing "accepts valid options"
        ;; Note: This is a unit test - we don't actually start the server
        (is (some? (main/start-mcp-server opts))))
      
      (testing "uses correct factory functions"
        ;; Verify that the function uses the expected factory functions
        (let [factory-called (atom false)]
          (with-redefs [core/build-and-start-mcp-server (fn [opts factory-fns]
                                                       (reset! factory-called true)
                                                       (is (contains? factory-fns :make-tools-fn))
                                                       (is (contains? factory-fns :make-prompts-fn))
                                                       (is (contains? factory-fns :make-resources-fn))
                                                       {:status "mock"})]
            (main/start-mcp-server opts)
            (is @factory-called)))))))

(deftest test-code-review-prompt-example
  (testing "Example code review prompt"
    (let [prompt-fn (main/code-review-prompt-example)
          request-args {"file-path" "src/test.clj" "focus-areas" "performance"}
          mock-clj-result-k (fn [result] result)]
      
      (testing "returns valid prompt structure"
        (let [prompt-result (prompt-fn nil request-args mock-clj-result-k)]
          (is (map? prompt-result))
          (is (contains? prompt-result :name))
          (is (contains? prompt-result :description))
          (is (contains? prompt-result :arguments))
          (is (= "code-review-prompt" (:name prompt-result))))
      
      (testing "generates appropriate messages"
        (let [prompt-result (prompt-fn nil request-args mock-clj-result-k)]
          (is (contains? prompt-result :messages))
          (is (vector? (:messages prompt-result)))
          (is (pos? (count (:messages prompt-result))))
          (let [messages (:messages prompt-result)
                first-message (first messages)]
            (is (contains? first-message :role))
            (is (contains? first-message :content))
            (is (= :user (:role first-message)))
            (is (string? (:content first-message)))
            (is (re-find #"src/test.clj" (:content first-message)))
            (is (re-find #"performance" (:content first-message))))))))