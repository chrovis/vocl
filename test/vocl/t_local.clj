(ns vocl.t-local
  (:use midje.sweet)
  (:require [clojure.test :refer :all]
            [vocl.local :as local]
            [vocl.core :refer :all]))

(def hello (promise))

(defhandlers handlers
  [:CALL "hello" (fn [body session]
                   (is (empty? body))
                   (deliver hello :done))])

(def session (atom nil))

(with-state-changes [(before :facts (reset! session (local/start handlers)))
                     (after :facts (do (local/stop @session)
                                       (reset! session nil)))]
  (fact "call handler"
        (do (call-handler! @session :CALL "hello")
            (Thread/sleep 200)
            @hello) => :done))
