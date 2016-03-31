(ns dev
  (:require (clojure
             [pprint :refer [pprint]]
             [test :as t])
            [clojure.core.match :refer [match]]
            [clojure.tools.namespace.repl :as tn]
            [boot.core :refer [load-data-readers!]]
            [mount.core :as mount :refer [defstate]]
            [mount.tools.graph :refer [states-with-deps]]
            [aristotl
             [util :refer [with-logging-status]]
             [db]
             [spider]]))


(defn start []
  (with-logging-status)
  (mount/start #'aristotl.spider/crawler #'aristotl.db/conn))

(defn stop []
  (mount/stop))

(defn refresh []
  (stop)
  (tn/refresh))

(defn refresh-all []
  (stop)
  (tn/refresh-all))

(defn go
  "starts all states defined by defstate"
  []
  (start)
  :ready)

(defn reset
  "stops all states defined by defstate, reloads modified source files, and restarts the states"
  []
  (stop)
  (tn/refresh :after 'dev/go))

(def ^{:arglists '([ns] [:all])}
  retest
  "Refreshes and runs tests on namespaces. When :all is the only arg, runs all
  tests. When args is a list of quoted namespaces, args is applied to
  clojure.test/run-tests"
  (fn [& args]
    (refresh)
    (match [args]
      [(:or [] nil)]  (println "Please supply quoted namespaces or `:all' to run tests.")
      [([:all] :seq)] (t/run-all-tests)
      [([& ns] :seq)] (apply t/run-tests ns))))

(load-data-readers!)
