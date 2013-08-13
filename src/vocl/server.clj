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
    (receive-all ch #(task (handling % session handlers)))))

; handshake contains a hashmap like
;
; {:remote-addr 0:0:0:0:0:0:0:1,
;  :scheme :http,
;  :websocket true,
;  :request-method :get,
;  :query-string nil,
;  :content-type nil,
;  :keep-alive? true,
;  :websocket? true,
;  :uri /,
;  :server-name localhost,
;  :headers {HTTP_HEADER},
;  :content-length nil,
;  :server-port 8008,
;  :character-encoding nil,
;  :body nil}

(defn uri [port]
  (format "ws://0.0.0.0:%d" port))

(defn start [port handlers]
  (let [server (start-http-server
                (fn [ch handshake] (connected ch handshake handlers))
                {:port port :websocket true})]
    server))

(defn stop [server]
  (server))
