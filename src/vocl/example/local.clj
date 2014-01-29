(ns vocl.example.local
  (:use [vocl.core]
        [vocl.example.ping])
  (:require [vocl.local :as local]))

(def my-session (promise))

(defhandlers handlers
  [:CALL "hello" (fn [body session]
                  (println "hello" body))])

(defn start []
  (let [s (local/start handlers)]
    (deliver my-session s)))

(defn stop []
  (realized? my-session ;; TODO
             ))

(defn -main []
  (start)
  (future (when (realized? my-session)
            (call-handler! @my-session :CALL "hello")
            (Thread/sleep 3000)
            (call-handler! @my-session :CALL "hello")
            (Thread/sleep 3000)
            (call-handler! @my-session :CALL "hello")
            (System/exit 0)))
  (println "Started"))
