(defproject vocl "0.1.0-SNAPSHOT"
  :description "a library for client/server communication via WebSocket"
  :url "https://chrov.is"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [aleph "0.3.0"]]
  :plugins [[lein-midje "3.1.1"]]
  :profiles {:dev {:dependencies [[midje "1.5.1"]]}})
