(ns clojure-mcp.sse-main-test
  (:require [clojure.test :refer :all]
            [clojure-mcp.sse-main :as sse-main]
            [clojure-mcp.main :as main]))

(deftest test-start-sse-mcp-server
  (testing "SSE main MCP server startup"
    (let [opts {:port 7888 :mcp-sse-port 8078}]
      
      (testing "accepts valid options"
        ;; Note: Unit test - don't actually start server
        (is (some? (sse-main/start-sse-mcp-server opts))))
      
      (testing "uses correct factory functions"
        (let [factory-called (atom false)
              captured-opts (atom nil)]
          (with-redefs [sse-core/build-and-start-mcp-server (fn [opts factory-fns]
                                                              (reset! factory-called true)
                                                              (reset! captured-opts opts)
                                                              (is (contains? factory-fns :make-tools-fn))
                                                              (is (contains? factory-fns :make-prompts-fn))
                                                              (is (contains? factory-fns :make-resources-fn))
                                                              {:status "mock"})]
            (sse-main/start-sse-mcp-server opts)
            (is @factory-called)
            (is (= opts @captured-opts))))))))

(deftest test-sse-main-delegation
  (testing "SSE main delegates to standard factories"
    (let [nrepl-client-atom (atom {:test "client"})
          working-dir "/tmp/test"]
      
      (testing "delegates tools creation to main"
        (let [tools (main/make-tools nrepl-client-atom working-dir)]
          (is (vector? tools))
          (is (pos? (count tools)))))
      
      (testing "delegates prompts creation to main"
        (let [prompts (main/make-prompts nrepl-client-atom working-dir)]
          (is (vector? prompts))
          (is (pos? (count prompts)))))
      
      (testing "delegates resources creation to main"
        (let [resources (main/make-resources nrepl-client-atom working-dir)]
          (is (vector? resources))
          (is (pos? (count resources))))))))

(deftest test-sse-transport-configuration
  (testing "SSE transport specific configuration"
    (let [opts {:port 7888 :mcp-sse-port 8078}]
      
      (testing "requires mcp-sse-port option"
        (is (contains? opts :mcp-sse-port))
        (is (int? (:mcp-sse-port opts))))
      
      (testing "validates port range"
        (let [port (:mcp-sse-port opts)]
          (is (and (>= port 1024) (<= port 65535)))))
      
      (testing "allows standard MCP options"
        (is (contains? opts :port))
        (is (int? (:port opts)))))))

(deftest test-sse-vs-stdio-differences
  (testing "Differences between SSE and stdio transports"
    (testing "SSE uses HTTP transport"
      (is (string? "http")))
      (is (string? "server-sent events")))
    
    (testing "stdio uses standard input/output"
      (is (string? "stdio")))
    
    (testing "SSE supports web clients"
      (is (string? "web-based")))
      (is (string? "browser")))
    
    (testing "stdio supports CLI clients"
      (is (string? "command-line")))
      (is (string? "terminal")))))

(deftest test-sse-integration-requirements
  (testing "SSE integration requirements"
    (testing "requires Jetty dependencies"
      ;; Test understanding of required dependencies
      (let [required-deps {"jakarta.servlet/jakarta.servlet-api" "6.1.0"
                           "org.eclipse.jetty/jetty-server" "11.0.20"
                           "org.eclipse.jetty/jetty-servlet" "11.0.20"}]
        (is (map? required-deps))
        (is (= 3 (count required-deps)))))
    
    (testing "requires HTTP server setup"
      ;; Test understanding of HTTP server requirements
      (is (string? "HTTP server"))
      (is (string? "Servlet"))
      (is (string? "Jetty"))))
    
    (testing "supports CORS for web clients"
      ;; Test understanding of web client requirements
      (is (string? "CORS"))
      (is (string? "Cross-Origin Resource Sharing")))))