(ns example.vocl-client
  (:use [vocl.core]
        [example.ping])
  (:require [vocl.client :as client]))

(def uri "ws://0.0.0.0:8008")

(defhandlers handlers
  [:POST "pong" handle-pong])

(def my-session (atom nil))

(defn start []
  (let [s (client/start uri handlers)]
    (reset! my-session s)))

(defn stop []
  (when-not (nil? @my-session)
    ;; TODO
    ))

(defn -main []
  (start)
  (future (loop []
            (when-not (nil? @my-session) (send-ping @my-session))
            (Thread/sleep 3000)
            (recur)))
  (println "Connected to" uri))
