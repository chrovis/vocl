(ns vocl.t-client
  (:use midje.sweet)
  (:require [vocl.client :as client]
            [vocl.server :as server]
            [vocl.core :refer :all]))

(def pong (promise))

(defn handle-pong [body session]
  (deliver pong :done))

(defhandlers handlers
  [:POST "pong" handle-pong])

(defhandlers ping-handlers
  [:POST "ping" (fn [body session]
                  (send-request! session :POST "pong"))])

(def test-client (atom nil))
(def test-server (atom nil))

(def port 18009)
(def uri (format "ws://0.0.0.0:%d" port))

(defn setup
  []
  (reset! test-server (server/start port ping-handlers))
  (Thread/sleep 1000)
  (reset! test-client (client/start uri handlers))
  (Thread/sleep 1000))

(defn shutdown
  []
  (client/stop @test-client)
  (reset! test-client nil)
  (server/stop @test-server)
  (reset! test-server nil))

(with-state-changes [(before :facts (setup))
                     (after :facts (shutdown))]
  (fact "receive request"
   (do (send-request! @test-client :POST "ping")
       (Thread/sleep 200)
       @pong) => :done))
