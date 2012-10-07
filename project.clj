(defproject xinshi "0.1.0-SNAPSHOT"
  :description "Messenger app backend"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [compojure "1.1.1"]
                 [clj-json "0.5.0"]
                 [clojurewerkz/neocons "1.0.2"]]
  :plugins [[lein-ring "0.7.5"]]
  :ring {:handler xinshi.handler/app}
  :dev-dependencies [[ring-mock "0.1.2"]])
