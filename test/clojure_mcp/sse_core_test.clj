(ns clojure-mcp.sse-core-test
  (:require [clojure.test :refer :all]
            [clojure-mcp.sse-core :as sse-core]
            [clojure-mcp.main :as main]))

(deftest test-build-and-start-sse-mcp-server
  (testing "SSE MCP server building and startup"
    (let [opts {:port 7888 :mcp-sse-port 8078}
          factory-fns {:make-tools-fn (fn [client dir]
                                       (main/make-tools client dir))
                     :make-prompts-fn (fn [client dir]
                                        (main/make-prompts client dir))
                     :make-resources-fn (fn [client dir]
                                          (main/make-resources client))}]
      
      (testing "SSE server configuration is built correctly"
        (let [config (sse-core/build-sse-server-config opts factory-fns)]
          (is (map? config))
          (is (contains? config :tools))
          (is (contains? config :prompts))
          (is (contains? config :resources))
          (is (contains? config :sse-port))
          (is (= 8078 (:sse-port config)))
          (is (contains? config :jetty-server))))
      
      (testing "uses standard factory functions"
        (let [tools-result ((:make-tools-fn factory-fns) nil "/tmp")
              prompts-result ((:make-prompts-fn factory-fns) nil "/tmp")
              resources-result ((:make-resources-fn factory-fns) nil "/tmp")]
          (is (vector? tools-result))
          (is (vector? prompts-result))
          (is (vector? resources-result)))))))

(deftest test-sse-transport-setup
  (testing "Server-Sent Events transport setup"
    (let [port 8078
          sse-handler (sse-core/create-sse-handler {:tools [] :prompts [] :resources []})]
      
      (testing "creates SSE handler"
        (is (some? sse-handler))
        (is (fn? sse-handler)))
      
      (testing "configures Jetty server"
        (let [jetty-server (sse-core/create-jetty-server port sse-handler)]
          (is (some? jetty-server))
          (is (contains? jetty-server :server))
          (is (contains? jetty-server :port)))))))

(deftest test-sse-message-handling
  (testing "SSE message processing"
    (let [test-messages [{:type "initialize" :data {:protocolVersion "2024-11-05"}}
                       {:type "tools/list" :data {}}
                       {:type "tools/call" :data {:name "test-tool" :arguments {}}}]
          handler-state (atom {:connected-clients #{}})]
      
      (testing "handles initialize messages"
        (let [init-msg (first test-messages)
              response (sse-core/handle-sse-message init-msg handler-state)]
          (is (map? response))
          (is (contains? response :type))
          (is (= "initialized" (:type response)))))
      
      (testing "handles tools/list messages"
        (let [tools-msg (second test-messages)
              response (sse-core/handle-sse-message tools-msg handler-state)]
          (is (map? response))
          (is (contains? response :type))
          (is (= "tools/list" (:type response)))))
      
      (testing "handles tools/call messages"
        (let [call-msg (nth test-messages 2)
              response (sse-core/handle-sse-message call-msg handler-state)]
          (is (map? response))
          (is (contains? response :type))
          (is (= "tools/call" (:type response))))))))

(deftest test-sse-client-connection
  (testing "SSE client connection management"
    (let [client-id "test-client-123"
          connection-state (atom {:clients {}})]
      
      (testing "registers new client"
        (let [updated-state (sse-core/register-client connection-state client-id)]
          (is (contains? (:clients updated-state) client-id))
          (is (map? (get (:clients updated-state) client-id)))))
      
      (testing "unregisters client"
        (let [state-with-client (swap! connection-state assoc-in [:clients client-id] {:connected true})
              updated-state (sse-core/unregister-client state-with-client client-id)]
          (is (not (contains? (:clients updated-state) client-id)))))
      
      (testing "broadcasts to all clients"
        (let [clients {"client1" {:connected true}
                    "client2" {:connected true}}
              state-with-clients (assoc-in @connection-state [:clients] clients)
              message {:type "test" :data "broadcast"}
              broadcast-results (sse-core/broadcast-to-clients state-with-clients message)]
          (is (= 2 (count broadcast-results))))))))

(deftest test-sse-error-handling
  (testing "SSE error handling"
    (testing "connection errors are handled gracefully"
      (let [error-result (sse-core/handle-connection-error "Connection failed")]
        (is (map? error-result))
        (is (contains? error-result :error))
        (is (contains? error-result :type))
        (is (= "error" (:type error-result)))))
    
    (testing "invalid message format errors"
      (let [invalid-messages ["not a map" nil {}]
            error-results (map #(sse-core/handle-invalid-message %) invalid-messages)]
        (doseq [result error-results]
          (is (map? result))
          (is (contains? result :error))
          (is (= "error" (:type result))))))
    
    (testing "tool execution errors"
      (let [tool-error {:name "test-tool" :error "Tool execution failed"}
              error-response (sse-core/format-tool-error tool-error)]
        (is (map? error-response))
        (is (contains? error-response :error))
        (is (contains? error-response :id))))))

(deftest test-sse-protocol-compliance
  (testing "SSE protocol compliance"
    (testing "follows MCP specification"
      (let [protocol-version "2024-11-05"
            capabilities {:tools {:listChanged true}
                        :prompts {:listChanged true}
                        :resources {:listChanged true}}
            server-info (sse-core/get-server-info protocol-version capabilities)]
        (is (map? server-info))
        (is (contains? server-info :protocolVersion))
        (is (contains? server-info :capabilities))
        (is (= protocol-version (:protocolVersion server-info)))
        (is (map? (:capabilities server-info)))))
    
    (testing "proper SSE event formatting"
      (let [event-data {:type "tools/call" :data {:name "test"}}
            sse-event (sse-core/format-sse-event event-data)]
        (is (string? sse-event))
        (is (re-find #"^data:" sse-event))
        (is (re-find #"\n\n$" sse-event)))))))