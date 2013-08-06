(ns vocl.pack)

;; serialize/deserialize

;; TODO improve format and compress message size

(defn freeze [data] (prn-str data))
(defn thaw [data] (read-string data))
