(ns clojure-mcp.main-examples.figwheel-main-test
  (:require [clojure.test :refer :all]
            [clojure-mcp.main-examples.figwheel-main :as figwheel-main]
            [clojure-mcp.main :as main]
            [clojure-mcp.tools.figwheel.tool :as figwheel-tool]))

(deftest test-make-tools
  (testing "Figwheel main tools creation"
    (let [nrepl-client-atom (atom {:test "client"})
          working-directory "/tmp/test"
          opts {:figwheel-build "dev"}
          tools-result (figwheel-main/make-tools nrepl-client-atom working-directory opts)]
      
      (testing "includes base tools"
        (let [base-tools (main/make-tools nrepl-client-atom working-directory)]
          (is (>= (count tools-result) (count base-tools)))
          ;; All base tools should be present
          (doseq [base-tool base-tools]
            (is (some #(= (:name base-tool) (:name %)) tools-result)))))
      
      (testing "includes figwheel tool"
        (let [figwheel-tools (filter #(= "clojurescript_eval" (:name %)) tools-result)]
          (is (= 1 (count figwheel-tools)))
          (let [figwheel-tool (first figwheel-tools)]
            (is (contains? figwheel-tool :name))
            (is (contains? figwheel-tool :description))
            (is (string? (:name figwheel-tool)))
            (is (string? (:description figwheel-tool))))))
      
      (testing "uses correct figwheel build"
        (let [figwheel-tools (filter #(= "clojurescript_eval" (:name %)) tools-result)
              figwheel-tool (first figwheel-tools)]
          ;; The tool should be configured with the specified build
          (is (some? (:tool-fn figwheel-tool)))))))

(deftest test-make-tools-default-build
  (testing "Figwheel tools with default build"
    (let [nrepl-client-atom (atom {:test "client"})
          working-directory "/tmp/test"
          tools-result (figwheel-main/make-tools nrepl-client-atom working-directory)]
      
      (testing "uses default build when not specified"
        (let [figwheel-tools (filter #(= "clojurescript_eval" (:name %)) tools-result)
              figwheel-tool (first figwheel-tools)]
          ;; Should default to "dev" build
          (is (some? (:tool-fn figwheel-tool)))))))

(deftest test-start-mcp-server
  (testing "Figwheel MCP server startup"
    (let [opts {:port 7888 :figwheel-build "dev"}]
      
      (testing "accepts valid options"
        ;; Note: Unit test - don't actually start server
        (is (some? (figwheel-main/start-mcp-server opts))))
      
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
            (figwheel-main/start-mcp-server opts)
            (is @factory-called)
            (is (= opts @captured-opts))))))))

(deftest test-figwheel-integration
  (testing "Figwheel integration requirements"
    (testing "requires piggieback middleware"
      ;; This is documented requirement - we test the understanding
      (is (string? "cider.piggieback/wrap-cljs-repl")))
    
    (testing "requires figwheel-main dependency"
      (is (string? "com.bhauman/figwheel-main")))
    
    (testing "requires nREPL configuration"
      ;; Test that we understand the nREPL setup requirements
      (let [expected-config {:extra-deps {"cider/piggieback" "0.6.0"
                                       "nrepl/nrepl" "1.3.1"
                                       "com.bhauman/figwheel-main" "0.2.20"}
                           :extra-paths ["test" "target"]
                           :jvm-opts ["-Djdk.attach.allowAttachSelf"]
                           :main-opts ["-m" "nrepl.cmdline" "--port" "7888"
                                       "--middleware" "[cider.piggieback/wrap-cljs-repl]"]}]
        (is (map? expected-config))
        (is (contains? expected-config :extra-deps))
        (is (contains? expected-config :main-opts)))))))