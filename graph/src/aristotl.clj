(ns aristotl
  "The entrypoint."
  (:gen-class)
  (:require [clojure.tools.nrepl.server :refer [start-server stop-server]]
            [mount.core :as mount :refer [defstate]]
            [aristotl
             [spider]]))

;; example on creating a network REPL
(defn- start-nrepl [{:keys [host port]}]
  (start-server :bind host :port port))

;; nREPL is just another simple state
(defstate nrepl
  :start (start-nrepl)
  :stop (stop-server nrepl))

;; example of an app entry point
(defn -main [& args]
  (mount/start))
