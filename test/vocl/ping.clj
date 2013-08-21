(ns vocl.ping
  (:require [clojure.test :refer :all]
            [vocl.core :refer :all]))

;; handle ping/pong

(def ping (promise))
(def pong (promise))

(defn handle-ping [body session]
  (is (not (nil? body)))
  (println (:time body))
  (is (number? (:time body)))
  (deliver ping :done))

(defn handle-pong [body session]
  (is (not (nil? body)))
  (is (number? (:time body)))
  (deliver pong :done))

;; send

(defn send-ping! [session]
  (let [body {:time (System/currentTimeMillis)}]
    (send-request! session :POST "ping" body)
    nil))

(defn call-ping! [session]
  (let [body {:time (System/currentTimeMillis)}]
    (call-handler! session :POST "ping" body)
    nil))

(defn send-pong! [session]
  (let [body {:time (System/currentTimeMillis)}]
    (send-request! session :POST "pong" body)
    nil))

(defn call-pong! [session]
  (let [body {:time (System/currentTimeMillis)}]
    (call-handler! session :POST "pong" body)
    nil))
