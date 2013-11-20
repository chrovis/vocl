(ns vocl.t-auth
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
(def test-client-valid (atom nil))
(def test-client-invalid (atom nil))

(def port 18008)
(def uri (format "ws://0.0.0.0:%d" port))

(defn auth
  [handshake]
  (let [user (get handshake "user")
        cred (get handshake "cred")]
    (if (and (= user "foo@example.com")
             (= cred "pa55w0rd"))
      {:user "foo@example.com"}
      nil)))

(defn started
  [user-info]
  (assoc user-info :message "hello world"))

(defn setup
  []
  (reset! test-server (server/start port handlers auth started))
  (Thread/sleep 200)
  (reset! test-client-invalid (client/start uri none {:user "bar@example.com"
                                                      :cred "none"}))
  (Thread/sleep 200)
  (reset! test-client-valid (client/start uri none {:user "foo@example.com"
                                                    :cred "pa55w0rd"}))
  (Thread/sleep 200))

(defn shutdown
  []
  (client/stop @test-client-valid)
  (reset! test-client-valid nil)
  (server/stop @test-server)
  (reset! test-server nil))

(with-state-changes [(before :facts (setup))
                     (after :facts (shutdown))]
  (fact "send request"
        (do (send-request! @test-client-invalid :POST "ping")
            (deref ping 1000 :failed)) => :failed
        (do (send-request! @test-client-valid :POST "ping")
            (deref ping 100 nil)) => :done))
