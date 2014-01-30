(ns vocl.example.client
  (:use [vocl.core]
        [vocl.example.ping])
  (:require [vocl.client :as client]))

(def uri "ws://0.0.0.0:8008")

(def my-session (atom nil))

(defhandlers handlers
  [:CALL "start" (fn [body session]
                   (call-handler! session :CALL "hello"))]
  [:CALL "stop" (fn [body session]
                  (println "bye"))]
  [:CALL "hello" (fn [body session]
                  (println "hello" body))]
  [:POST "pong" handle-pong])

(defn start []
  (let [s (client/start uri handlers {:user "foo@example.com"
                                      :cred "pa55w0rd"})]
    (reset! my-session s)))

(defn stop []
  (when-not (nil? @my-session)
    (client/stop @my-session)
    (reset! my-session nil)))

(defn -main []
  (start)
  (println "Connected to" uri))
