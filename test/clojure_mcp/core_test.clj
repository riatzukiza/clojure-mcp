(ns clojure-mcp.core-test
  (:require [clojure.test :refer :all]
            [clojure-mcp.core :as core]))

(deftest test-core-namespace
  (testing "Core namespace loads correctly"
    (is (some? (find-ns 'clojure-mcp.core)))))