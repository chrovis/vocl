(ns vocl.client
  (:use [lamina core executor]
        [aleph.http]
        [vocl.route :only [routing]]
        [vocl.pack :only [freeze thaw]]))

(defn- connect [url options]
  (let [client (websocket-client {:url url
                                  :custom-headers options})
        ch (wait-for-result client)]
    {:client client :channel ch}))

(defn handle-local [req session handlers]
  (routing handlers req session))

(defn- handle-remote [req-string session handlers]
  (let [req (thaw req-string)
        result (routing handlers req session)]
    (if (nil? result) nil (freeze result))))

(defn start
  ([uri handlers]
     (start uri handlers nil))
  ([uri handlers options]
     (let [remote-session (connect uri options)
           local-ch (channel)
           session (assoc remote-session :local local-ch)]
       (receive-all (:channel session) #(handle-remote % session handlers))
       (receive-all (:local session) #(handle-local % session handlers))
       (on-realized (:client session)
                    (fn [c]
                      (on-realized (closed-result c)
                                   (fn [ch]
                                     (enqueue (:local session) {:method :CALL
                                                                :key "stop"
                                                                :body {}}))
                                   nil)
                      (enqueue (:local session) {:method :CALL
                                                 :key "start"
                                                 :body {}})

                      nil)
                    nil)
       session)))

(defn stop [session]
  (close-on-idle 100 (:channel session)))
