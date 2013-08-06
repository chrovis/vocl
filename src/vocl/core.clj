(ns vocl.core
  (:use [lamina core executor]
        [aleph.http]
        [vocl.pack :only [freeze thaw]]))

;; send

(defn- make-request [method key body]
  {:pre [(keyword? method)
         (string? key)
         (or (map? body) (vector? body))]}
  {:method method
   :key key
   :body body})

(defn- -send-request! [session method key body]
  (enqueue (:channel session)
           (freeze (make-request method key body))))

(defn send-request!
  ([session method key]
     (-send-request! session method key {}))
  ([session method key body]
     (-send-request! session method key body)))

;; receive handler

(defn- make-handler [method name f]
  {[method name] f})

(defn make-handlers [& handlers]
  (apply merge (map #(apply make-handler %) handlers)))

(defmacro defhandlers [name & handlers]
  `(def ~name (make-handlers ~@handlers)))
