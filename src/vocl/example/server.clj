(ns vocl.example.server
  (:use [vocl.core]
        [vocl.example.ping])
  (:require [vocl.server :as server]))

(def port 8008)

(defhandlers handlers
  [:POST "ping" handle-ping])

(def my-server (atom nil))

(defn- auth
  [handshake]
  (let [user (get handshake "user")
        cred (get handshake "cred")]
    (if (and (= user "foo@example.com")
             (= cred "pa55w0rd"))
      {:user "foo@example.com"}
      nil)))

(defn- connected
  [session]
  (assoc session :user-info
         (assoc (:user-info session) :message "hello world")))

(defn- disconnected
  [session]
  (println "disconnected:" (:user-info session)))

(defn start []
  (let [s (server/start port handlers auth connected disconnected)]
    (reset! my-server s)))

(defn stop []
  (when-not (nil? @my-server)
    (server/stop @my-server)
    (reset! my-server nil)))

(defn -main []
  (start)
  (println "Running with" (server/uri port)))
