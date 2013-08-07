(ns vocl.route)

;; request is like following
;;
;; {:method (or :GET :PORT :DELETE)
;;  :key "a_unique_key"
;;  :body {:key1 "value", :key2 12345}}

(defn routing [handlers req & args]
  (let [routes-key [(:method req) (:key req)]
        f (handlers routes-key)]
    (when-not (nil? f) (apply f (concat [(:body req)] args)))))
