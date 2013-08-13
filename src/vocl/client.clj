(ns vocl.client
  (:use [lamina core executor]
        [aleph.http]
        [vocl.route :only [routing]]
        [vocl.pack :only [freeze thaw]]))

(defn- connect [url]
  (let [client (websocket-client {:url url})
        ch (wait-for-result client)]
    {:client client :channel ch}))

(defn- disconnect [session]
  ;; TODO
  )

(defn local-handling [req session handlers]
  (routing handlers req session))

(defn- handling [req-string session handlers]
  (let [req (thaw req-string)
        result (routing handlers req session)]
    (if (nil? result) nil (freeze result))))

(defn start [uri handlers]
  (let [remote-session (connect uri)
        local-ch (channel)
        session (assoc remote-session :local local-ch)]
    (receive-all (:channel session) #(task (handling % session handlers)))
    (receive-all (:local session) #(task (local-handling % session handlers)))
    (on-realized (:client session)
                 (do (enqueue (:local session) {:method :CALL
                                                :key "start"
                                                :body {}})
                     nil)
                 nil)
    session))

(defn stop [session]
  ;; TODO
  )
