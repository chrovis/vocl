(ns vocl.t-server
  (:use midje.sweet)
  (:require [vocl.server :as server]
            [vocl.client :as client]
            [vocl.core :refer :all]))

(def ping (promise))

(defn handle-ping [body session]
  (deliver ping :done))

(defhandlers handlers
  [:POST "ping" handle-ping])

(defhandlers none
  [:DUMMY "dummy" (fn [_ _])])

(def test-server (atom nil))
(def test-client (atom nil))

(def port 18008)
(def uri (format "ws://0.0.0.0:%d" port))

(defn setup
  []
  (reset! test-server (server/start port handlers))
  (Thread/sleep 1000)
  (reset! test-client (client/start uri none))
  (Thread/sleep 1000))

(defn shutdown
  []
  (client/stop @test-client)
  (reset! test-client nil)
  (server/stop @test-server)
  (reset! test-server nil))

(with-state-changes [(before :facts (setup))
                     (after :facts (shutdown))]
  (fact "send request"
   (do (send-request! @test-client :POST "ping")
       (Thread/sleep 200)
       @ping) => :done))
