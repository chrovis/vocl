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

(defn- connected
  [ch handshake handlers user-info init]
  (let [session (init {:channel ch :handshake handshake :user-info user-info})]
    (receive-all ch #(handling % session handlers))
    session))

(defn uri [port]
  (format "ws://0.0.0.0:%d" port))

(defn start
  ([port handlers]
     (start port handlers nil (fn [session] session)))
  ([port handlers auth init]
      (let [server (start-http-server
                    (fn [ch handshake]
                      (if (nil? auth)
                        (connected ch handshake handlers nil init)
                        (let [user-info (auth (:headers handshake))]
                          (if (nil? user-info)
                            (close ch)
                            (connected ch handshake handlers user-info init)))))
                    {:port port :websocket true})]
        server)))

(defn stop [server]
  (server))
