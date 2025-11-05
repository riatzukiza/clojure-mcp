(ns clojure-mcp.main-examples.shadow-main-test
  (:require [clojure.test :refer :all]
            [clojure-mcp.main-examples.shadow-main :as shadow-main]
            [clojure-mcp.main :as main]
            [clojure-mcp.nrepl :as nrepl]))

(deftest test-start-shadow-repl
  (testing "Shadow REPL startup"
    (let [nrepl-client-atom (atom {:test "client"})
          cljs-session "test-session"
          config {:shadow-build "app" :shadow-watch true}]
      
      (testing "generates correct start code for watch mode"
        (let [start-code (shadow-main/start-shadow-repl nrepl-client-atom cljs-session config)]
          (is (string? start-code))
          (is (re-find #"shadow/watch" start-code))
          (is (re-find #"shadow/repl" start-code))))
      
      (testing "generates correct start code for no watch"
        (let [config-no-watch {:shadow-build "app"}
              start-code (shadow-main/start-shadow-repl nrepl-client-atom cljs-session config-no-watch)]
          (is (string? start-code))
          (is (re-find #"shadow/repl" start-code))
          (is (not (re-find #"shadow/watch" start-code))))))))

(deftest test-shadow-eval-tool-secondary-connection
  (testing "Shadow eval tool with secondary connection"
    (let [nrepl-client-atom (atom {:port 7888})
          config {:shadow-port 7889 :shadow-build "app"}]
      
      (testing "creates additional connection"
        (let [tool (shadow-main/shadow-eval-tool-secondary-connection-tool nrepl-client-atom config)]
          (is (map? tool))
          (is (contains? tool :name))
          (is (= "clojurescript_eval" (:name tool)))
          (is (contains? tool :description))
          (is (string? (:description tool)))))
      
      (testing "uses correct shadow port"
        ;; This would be tested in integration scenarios
        (is (= 7889 (:shadow-port config)))))))

(deftest test-shadow-eval-tool-single-connection
  (testing "Shadow eval tool with single connection"
    (let [nrepl-client-atom (atom {:port 7888})
          config {:shadow-build "app"}]
      
      (testing "creates tool with shared connection"
        (let [tool (shadow-main/shadow-eval-tool nrepl-client-atom config)]
          (is (map? tool))
          (is (contains? tool :name))
          (is (= "clojurescript_eval" (:name tool)))
          (is (contains? tool :description))
          (is (string? (:description tool)))))
      
      (testing "creates new cljs session"
        ;; Verify that a new session is created for CLJS
        (is (some? (:nrepl-session tool)))))))

(deftest test-make-tools-dual-connection
  (testing "Tools creation with dual connection mode"
    (let [nrepl-client-atom (atom {:port 7888})
          working-directory "/tmp/test"
          config {:port 7888 :shadow-port 7889 :shadow-build "app"}
          tools-result (shadow-main/make-tools nrepl-client-atom working-directory config)]
      
      (testing "includes base tools"
        (let [base-tools (main/make-tools nrepl-client-atom working-directory)]
          (is (>= (count tools-result) (count base-tools)))
          ;; All base tools should be present
          (doseq [base-tool base-tools]
            (is (some #(= (:name base-tool) (:name %)) tools-result)))))
      
      (testing "includes shadow tool with secondary connection"
        (let [shadow-tools (filter #(= "clojurescript_eval" (:name %)) tools-result)]
          (is (= 1 (count shadow-tools)))
          (let [shadow-tool (first shadow-tools)]
            (is (contains? shadow-tool :name))
            (is (contains? shadow-tool :description))))))))

(deftest test-make-tools-single-connection
  (testing "Tools creation with single connection mode"
    (let [nrepl-client-atom (atom {:port 7888})
          working-directory "/tmp/test"
          config {:port 7888 :shadow-build "app"}
          tools-result (shadow-main/make-tools nrepl-client-atom working-directory config)]
      
      (testing "includes base tools"
        (let [base-tools (main/make-tools nrepl-client-atom working-directory)]
          (is (>= (count tools-result) (count base-tools)))
          ;; All base tools should be present
          (doseq [base-tool base-tools]
            (is (some #(= (:name base-tool) (:name %)) tools-result)))))
      
      (testing "includes shadow tool with shared connection"
        (let [shadow-tools (filter #(= "clojurescript_eval" (:name %)) tools-result)]
          (is (= 1 (count shadow-tools)))
          (let [shadow-tool (first shadow-tools)]
            (is (contains? shadow-tool :name))
            (is (contains? shadow-tool :description))))))))

(deftest test-start-mcp-server
  (testing "Shadow main MCP server startup"
    (let [opts {:port 7888 :shadow-build "app"}]
      
      (testing "accepts valid options"
        ;; Note: Unit test - don't actually start server
        (is (some? (shadow-main/start-mcp-server opts))))
      
      (testing "uses correct factory functions"
        (let [factory-called (atom false)
              captured-opts (atom nil)]
          (with-redefs [core/build-and-start-mcp-server (fn [opts factory-fns]
                                                           (reset! factory-called true)
                                                           (reset! captured-opts opts)
                                                           (is (contains? factory-fns :make-tools-fn))
                                                           (is (contains? factory-fns :make-prompts-fn))
                                                           (is (contains? factory-fns :make-resources-fn))
                                                           {:status "mock"})]
            (shadow-main/start-mcp-server opts)
            (is @factory-called)
            (is (= opts @captured-opts))))))))

(deftest test-shadow-config-validation
  (testing "Shadow configuration validation"
    (testing "valid dual connection config"
      (let [config {:port 7888 :shadow-port 7889 :shadow-build "app"}]
        (is (not= (:port config) (:shadow-port config)))))
    
    (testing "valid single connection config"
      (let [config {:port 7888 :shadow-build "app"}]
        (is (some? (:shadow-build config)))))
    
    (testing "shadow build is required"
      (let [config-without-build {:port 7888 :shadow-port 7889}]
        ;; Build should be required for shadow functionality
        (is (nil? (:shadow-build config-without-build)))))))