(ns vocl.local
  (:use [lamina core executor]
        [vocl.route :only [routing]]))

(defn- handling [req session handlers]
  (routing handlers req session))

(defn start [handlers]
  (let [ch (channel)
        session {:local ch :channel ch}]
    (receive-all ch #(task (handling % session handlers)))
    session))

(defn stop [session]
  (close (:local session)))
