(ns clojure-mcp.integration-test
  (:require [clojure.test :refer :all]
            [clojure-mcp.core :as core]
            [clojure-mcp.main :as main]
            [clojure-mcp.mock-client-test :as mock-client]
            [clojure-mcp.nrepl :as nrepl]
            [nrepl.server :as nrepl-server]))



(deftest test-full-mcp-workflow
  (testing "Complete MCP workflow integration"
    (let [server (nrepl-server/start-server :port 0)
          port (:port server)
          client (nrepl/create {:port port})
          client-atom (atom client)]
      (nrepl/start-polling client)
      (nrepl/eval-code client "(require 'clojure.repl)" identity)
      (try
        (let [mock-client (mock-client/create-mock-mcp-client client-atom)
              tools (main/make-tools client-atom "/tmp")
              prompts (main/make-prompts client-atom "/tmp")
              resources (main/make-resources client-atom "/tmp")]
      
      (testing "tools are created and functional"
        (is (pos? (count tools)))
        (doseq [tool tools]
          (is (contains? tool :name))
          (is (contains? tool :description))))
      
      (testing "prompts are created and functional"
        (is (pos? (count prompts)))
        (doseq [prompt prompts]
          (is (contains? prompt :name))
          (is (contains? prompt :description))))
      
       (testing "resources are created and functional"
         (is (pos? (count resources)))
         (doseq [resource resources]
           (is (contains? resource :uri))
           (is (contains? resource :name))
           (is (contains? resource :description)))))
        (finally
          (nrepl/stop-polling client)
          (nrepl-server/stop-server server))))))

(deftest test-tool-execution-workflow
  (testing "Tool execution workflow"
    (let [server (nrepl-server/start-server :port 0)
          port (:port server)
          client (nrepl/create {:port port})
          client-atom (atom client)]
      (nrepl/start-polling client)
      (nrepl/eval-code client "(require 'clojure.repl)" identity)
      (try
        (let [tools (main/make-tools client-atom "/tmp")
              eval-tool (some #(when (= "clojure_eval" (:name %)) %) tools)]
      
      (testing "finds clojure_eval tool"
        (is (some? eval-tool))
        (is (= "clojure_eval" (:name eval-tool))))
      
      (testing "tool execution works"
        (let [tool-fn (:tool-fn eval-tool)
              args {:code "(+ 1 2 3)"}
              result-promise (promise)]
          (tool-fn nil args
                   (fn [result error?]
                     (deliver result-promise {:result result :error? error?})))
           (let [result @result-promise]
             (is (false? (:error? result)))
             (is (some? (:result result))))))
        (finally
          (nrepl/stop-polling client)
          (nrepl-server/stop-server server))))))

(deftest test-nrepl-integration
  (testing "nREPL integration functionality"
    (let [server (nrepl-server/start-server :port 0)
          port (:port server)
          client (nrepl/create {:port port})]
      
      (testing "basic code evaluation"
        (let [result (nrepl/eval-code nrepl-client "(+ 1 2 3)" identity)]
          (is (= 6 result))))
      
      (testing "namespace operations"
        (let [ns-result (nrepl/eval-code nrepl-client "(ns *ns*)" identity)]
          (is (string? ns-result))))
      
       (testing "function definition and calling"
         (let [_ (nrepl/eval-code client "(defn test-fn [x] (* x 2))" identity)
               call-result (nrepl/eval-code client "(test-fn 5)" identity)]
           (is (= 10 call-result))))
        (finally
          (nrepl-server/stop-server server))))))

(deftest test-error-scenarios
  (testing "Error scenario handling"
    (let [server (nrepl-server/start-server :port 0)
          port (:port server)
          client (nrepl/create {:port port})
          client-atom (atom client)]
      (nrepl/start-polling client)
      (nrepl/eval-code client "(require 'clojure.repl)" identity)
      (try
        (let [tools (main/make-tools client-atom "/tmp")]
      
      (testing "invalid code evaluation"
        (let [eval-tool (some #(when (= "clojure_eval" (:name %)) %) tools)
              tool-fn (:tool-fn eval-tool)
              args {:code "(invalid syntax"}
              result-promise (promise)]
          (tool-fn nil args
                   (fn [result error?]
                     (deliver result-promise {:result result :error? error?})))
           (let [result @result-promise]
             (is (true? (:error? result)))
             (is (some? (:result result))))))
        (finally
          (nrepl/stop-polling client)
          (nrepl-server/stop-server server))))))

(deftest test-configuration-integration
  (testing "Configuration integration"
    (let [server (nrepl-server/start-server :port 0)
          port (:port server)
          client (nrepl/create {:port port})
          client-atom (atom client)]
      (nrepl/start-polling client)
      (nrepl/eval-code client "(require 'clojure.repl)" identity)
      (try
        (let [opts {:port port :host "localhost"}
              server-config (core/build-server-config 
                            opts
                            {:make-tools-fn (fn [client dir]
                                               (main/make-tools client dir))
                             :make-prompts-fn (fn [client dir]
                                                  (main/make-prompts client dir))
                             :make-resources-fn (fn [client dir]
                                                     (main/make-resources client))})]
      
      (testing "server config includes all components"
        (is (contains? server-config :tools))
        (is (contains? server-config :prompts))
        (is (contains? server-config :resources))
        (is (contains? server-config :nrepl-client-atom)))
      
      (testing "tools are properly configured"
        (let [tools (:tools server-config)]
          (is (vector? tools))
          (is (pos? (count tools)))))
      
      (testing "prompts are properly configured"
        (let [prompts (:prompts server-config)]
          (is (vector? prompts))
          (is (pos? (count prompts)))))
      
       (testing "resources are properly configured"
         (let [resources (:resources server-config)]
           (is (vector? resources))
           (is (pos? (count resources))))))
        (finally
          (nrepl/stop-polling client)
          (nrepl-server/stop-server server))))))

(deftest test-protocol-compliance
  (testing "MCP protocol compliance"
    (let [server (nrepl-server/start-server :port 0)
          port (:port server)
          client (nrepl/create {:port port})
          client-atom (atom client)]
      (nrepl/start-polling client)
      (nrepl/eval-code client "(require 'clojure.repl)" identity)
      (try
        (let [tools (main/make-tools client-atom "/tmp")
              prompts (main/make-prompts client-atom "/tmp")
              resources (main/make-resources client-atom "/tmp")]
      
      (testing "tools follow MCP specification"
        (doseq [tool tools]
          (is (contains? tool :name))
          (is (contains? tool :description))
          (is (or (contains? tool :inputSchema)
                   (contains? tool :input_schema)))
          (is (string? (:name tool)))
          (is (string? (:description tool))))
      
      (testing "prompts follow MCP specification"
        (doseq [prompt prompts]
          (is (contains? prompt :name))
          (is (contains? prompt :description))
          (is (contains? prompt :arguments))
          (is (vector? (:arguments prompt)))))
      
       (testing "resources follow MCP specification"
         (doseq [resource resources]
           (is (contains? resource :uri))
           (is (contains? resource :name))
           (is (contains? resource :description))
           (is (string? (:uri resource)))
           (is (string? (:name resource)))
           (is (string? (:description resource))))))
        (finally
          (nrepl/stop-polling client)
          (nrepl-server/stop-server server))))))