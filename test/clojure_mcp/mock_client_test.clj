(ns clojure-mcp.mock-client-test
  (:require [clojure.test :refer :all]
            [clojure-mcp.core :as core]
            [clojure-mcp.main :as main]
            [clojure-mcp.nrepl :as nrepl]
            [clojure.data.json :as json]
            [clojure.java.io :as io]))

;; Mock MCP client for testing
(defn create-mock-mcp-client
  "Creates a mock MCP client for testing server functionality"
  [nrepl-client-atom & [opts]]
  (let [client-opts (merge {:timeout 5000 :auto-reconnect false} opts)
        client-state (atom {:connected false
                        :server-info nil
                        :available-tools []
                        :available-prompts []
                        :available-resources []
                        :request-id 0})]
    
    (assoc client-opts
      :nrepl-client-atom nrepl-client-atom
      :client-state client-state
      :send-message (fn [message]
                      (let [request-id (swap! client-state update :request-id inc)
                            message-with-id (assoc message :id request-id)]
                        (swap! client-state assoc :last-sent-message message-with-id)
                        message-with-id))
      :wait-for-response (fn [timeout-ms]
                          (let [start-time (System/currentTimeMillis)
                                timeout (or timeout-ms (:timeout client-opts 5000))]
                            (loop []
                              (let [elapsed (- (System/currentTimeMillis) start-time)]
                                (if (>= elapsed timeout)
                                  {:error "timeout"}
                                  (let [response (get @client-state :last-response)]
                                    (if response
                                      response
                                      (do (Thread/sleep 100)
                                           (recur))))))))))

(defn mock-mcp-protocol
  "Mock MCP protocol handler for testing"
  [tools prompts resources]
  (let [protocol-state (atom {:initialized false
                             :tools tools
                             :prompts prompts
                             :resources resources})]
    
    {:handle-message (fn [message]
                      (let [method (:method message)]
                        (cond
                          (= method "initialize")
                          (do
                            (swap! protocol-state assoc :initialized true)
                            {:jsonrpc "2.0"
                             :id (:id message)
                             :result {:protocolVersion "2024-11-05"
                                      :capabilities {:tools {:listChanged true}
                                                  :prompts {:listChanged true}
                                                  :resources {:listChanged true}}}})
                          
                           (= method "tools/list")
                           {:jsonrpc "2.0"
                            :id (:id message)
                            :result {:tools (:tools @protocol-state)}}
                           
                           (= method "prompts/list")
                           {:jsonrpc "2.0"
                            :id (:id message)
                            :result {:prompts (:prompts @protocol-state)}}
                           
                           (= method "resources/list")
                           {:jsonrpc "2.0"
                            :id (:id message)
                            :result {:resources (:resources @protocol-state)}}
                          
                          :default
                          {:jsonrpc "2.0"
                           :id (:id message)
                           :error {:code -32601
                                   :message (str "Method not found: " method)}})))
     :get-state (fn [] @protocol-state)}))

(deftest test-mock-client-creation
  (testing "Mock MCP client creation"
    (let [nrepl-client-atom (atom {:test "client"})
          mock-client (create-mock-mcp-client nrepl-client-atom)]
      
      (testing "client has required structure"
        (is (map? mock-client))
        (is (contains? mock-client :nrepl-client-atom))
        (is (contains? mock-client :client-state))
        (is (contains? mock-client :send-message))
        (is (contains? mock-client :wait-for-response)))
      
      (testing "client state is initialized"
        (let [client-state (:client-state mock-client)]
          (is (false? (:connected client-state)))
          (is (nil? (:server-info client-state)))
          (is (empty? (:available-tools client-state)))
          (is (empty? (:available-prompts client-state)))
          (is (empty? (:available-resources client-state)))
          (is (zero? (:request-id client-state))))))))

(deftest test-mock-protocol-initialization
  (testing "Mock MCP protocol initialization"
    (let [tools [{:name "test-tool" :description "Test tool"}]
          prompts [{:name "test-prompt" :description "Test prompt"}]
          resources [{:uri "test://resource" :name "test" :description "Test resource"}]
          protocol (mock-mcp-protocol tools prompts resources)]
      
      (testing "protocol has required structure"
        (is (map? protocol))
        (is (contains? protocol :handle-message))
        (is (contains? protocol :get-state)))
      
      (testing "protocol state is initialized"
        (let [state ((:get-state protocol))]
          (is (false? (:initialized state)))
          (is (= tools (:tools state)))
          (is (= prompts (:prompts state)))
          (is (= resources (:resources state)))))
      
      (testing "handles initialize message"
        (let [init-msg {:jsonrpc "2.0" :id 1 :method "initialize"}
              response ((:handle-message protocol) init-msg)]
          (is (map? response))
          (is (= "2.0" (:jsonrpc response)))
          (is (= 1 (:id response)))
          (is (contains? response :result))
          (let [result (:result response)]
            (is (= "2024-11-05" (:protocolVersion result)))
            (is (contains? result :capabilities))))))))

(deftest test-mock-protocol-tools-list
  (testing "Mock protocol tools listing"
    (let [tools [{:name "tool1" :description "Tool 1"}
                  {:name "tool2" :description "Tool 2"}]
          prompts []
          resources []
          protocol (mock-mcp-protocol tools prompts resources)]
      
      (testing "handles tools/list message"
        (let [tools-list-msg {:jsonrpc "2.0" :id 2 :method "tools/list"}
              response ((:handle-message protocol) tools-list-msg)]
          (is (map? response))
          (is (= "2.0" (:jsonrpc response)))
          (is (= 2 (:id response)))
          (is (contains? response :result))
          (let [result (:result response)]
            (is (contains? result :tools))
            (is (= 2 (count (:tools result))))
            (is (= tools (:tools result)))))))))

(deftest test-mock-protocol-prompts-list
  (testing "Mock protocol prompts listing"
    (let [tools []
          prompts [{:name "prompt1" :description "Prompt 1"}
                   {:name "prompt2" :description "Prompt 2"}]
          resources []
          protocol (mock-mcp-protocol tools prompts resources)]
      
      (testing "handles prompts/list message"
        (let [prompts-list-msg {:jsonrpc "2.0" :id 3 :method "prompts/list"}
              response ((:handle-message protocol) prompts-list-msg)]
          (is (map? response))
          (is (= "2.0" (:jsonrpc response)))
          (is (= 3 (:id response)))
          (is (contains? response :result))
          (let [result (:result response)]
            (is (contains? result :prompts))
            (is (= 2 (count (:prompts result))))
            (is (= prompts (:prompts result)))))))))

(deftest test-mock-protocol-resources-list
  (testing "Mock protocol resources listing"
    (let [tools []
          prompts []
          resources [{:uri "resource1" :name "res1" :description "Resource 1"}
                    {:uri "resource2" :name "res2" :description "Resource 2"}]
          protocol (mock-mcp-protocol tools prompts resources)]
      
      (testing "handles resources/list message"
        (let [resources-list-msg {:jsonrpc "2.0" :id 4 :method "resources/list"}
              response ((:handle-message protocol) resources-list-msg)]
          (is (map? response))
          (is (= "2.0" (:jsonrpc response)))
          (is (= 4 (:id response)))
          (is (contains? response :result))
          (let [result (:result response)]
            (is (contains? result :resources))
            (is (= 2 (count (:resources result))))
            (is (= resources (:resources result)))))))))

(deftest test-mock-protocol-error-handling
  (testing "Mock protocol error handling"
    (let [tools []
          prompts []
          resources []
          protocol (mock-mcp-protocol tools prompts resources)]
      
      (testing "handles unknown method"
        (let [unknown-msg {:jsonrpc "2.0" :id 5 :method "unknown/method"}
              response ((:handle-message protocol) unknown-msg)]
          (is (map? response))
          (is (= "2.0" (:jsonrpc response)))
          (is (= 5 (:id response)))
          (is (contains? response :error))
          (let [error (:error response)]
            (is (= -32601 (:code error)))
            (is (re-find #"Method not found" (:message error)))))))))

)))
