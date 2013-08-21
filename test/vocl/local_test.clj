(ns vocl.local-test
  (:require [clojure.test :refer :all]
            [vocl.local :as local]
            [vocl.core :refer :all]
            [vocl.ping :refer :all]))

(defhandlers handlers
  [:POST "ping" (fn [body session]
                  (handle-ping body session)
                  (call-pong! session))]
  [:POST "pong" (fn [body session]
                  (handle-pong body session))]
  [:CALL "hello" (fn [body session]
                   (is (empty? body)))])

(deftest test-start-stop
  (let [session (local/start handlers)]
    (is (not (nil? session)))))

(deftest test-call
  (let [session (local/start handlers)]
    (call-handler! session :CALL "hello")))

(deftest test-ping-pong
  (let [session (local/start handlers)]
    (call-ping! session)
    (Thread/sleep 1000)
    (is (realized? ping))
    (is (realized? pong))))
