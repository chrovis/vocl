(ns vocl.server
    (:use [lamina core executor]
          [aleph.http]
          [vocl.route :only [routing]]
          [vocl.pack :only [freeze thaw]]))

(defn local-handling [req session handlers]
  (routing handlers req session))

(defn- handling [req-string session handlers]
  (let [req (thaw req-string)
        result (routing handlers req session)]
    (if (nil? result) nil (freeze result))))

(defn- connected [ch handshake handlers]
  (let [session {:channel ch :handshake handshake}]
    ;(receive-all ch #(task (handling % session handlers)))
    (receive-all ch #(handling % session handlers))))

(defn uri [port]
  (format "ws://0.0.0.0:%d" port))

(defn start
  ([port handlers]
     (start port handlers nil))
  ([port handlers auth]
      (let [server (start-http-server
                    (fn [ch handshake]
                      (if (nil? auth)
                        (connected ch handshake handlers)
                        (if (auth (:headers handshake))
                          (connected ch handshake handlers)
                          (close ch))))
                    {:port port :websocket true})]
        server)))

(defn stop [server]
  (server))
