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

(defn- handling [req-string session handlers]
  (let [req (thaw req-string)
        result (routing handlers req session)]
    (if (nil? result) nil (freeze result))))

(defn start [uri handlers]
  (let [session (connect uri)]
    (receive-all (:channel session) #(task (handling % session handlers)))
    session))

(defn stop [session]
  ;; TODO
  )
