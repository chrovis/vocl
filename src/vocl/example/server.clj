(ns vocl.example.server
  (:use [vocl.core]
        [vocl.example.ping])
  (:require [vocl.server :as server]))

(def port 8008)

(defhandlers handlers
  [:POST "ping" handle-ping])

(def my-server (atom nil))

(defn start []
  (let [s (server/start port handlers)]
    (reset! my-server s)))

(defn stop []
  (when-not (nil? @my-server)
    (server/stop @my-server)
    (reset! my-server nil)))

(defn -main []
  (start)
  (println "Running with" (server/uri port)))
