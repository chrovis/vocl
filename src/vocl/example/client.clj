(ns vocl.example.client
  (:use [vocl.core]
        [vocl.example.ping])
  (:require [vocl.client :as client]))

(def uri "ws://0.0.0.0:8008")

(def my-session (promise))

(defn- start-pinging
  []
  (future (when (realized? my-session)
            (Thread/sleep 3000)
            (send-ping @my-session)
            (Thread/sleep 3000)
            (send-ping @my-session)
            (Thread/sleep 3000)
            (send-ping @my-session)
            (System/exit 0))))

(defhandlers handlers
  [:CALL "start" (fn [body session]
                   (send-ping session)
                   (call-handler! session :CALL "hello")
                   (start-pinging))]
  [:CALL "hello" (fn [body session]
                  (println "hello" body))]
  [:POST "pong" handle-pong])

(defn start []
  (let [s (client/start uri handlers)]
    (deliver my-session s)))

(defn stop []
  (realized? my-session ;; TODO
             ))

(defn -main []
  (start)
  (println "Connected to" uri))
