(ns vocl.core-test
  (:require [clojure.test :refer :all]
            [vocl.core :refer :all]))

;; (deftest test-make-request
;;   (let [res (make-request :TEST "key" {:foo 1 :bar 2 :baz 3})]
;;     (is (= :TEST (:method res)))
;;     (is (= "key" (:key res)))
;;     (is (= {:foo 1 :bar 2 :baz 3} (:body res)))))
