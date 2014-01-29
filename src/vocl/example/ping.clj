(ns vocl.example.ping
  (:use [vocl.core]))

;; handle ping/pong

(defn handle-ping [body session]
  (println "receive ping" body)
  (send-request! session :POST "pong" {:time (System/currentTimeMillis)}))

(defn handle-pong [body session]
  (println "pong" body))

;; send

(defn send-ping [session]
  (let [body {:time (System/currentTimeMillis)}]
    (send-request! session :POST "ping" body)
    (println "ping" body)))
