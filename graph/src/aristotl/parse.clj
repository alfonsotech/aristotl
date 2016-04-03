(ns aristotl.parse
  "TThe parser receives messages from the spider and currently just prints the
  received URL"
  (:require [clojure.tools.logging :as log]
            [clojure.core.async :as async :refer [go >! <! >!! <!!]]
            [mount.core :refer [defstate]]))



(defn start-parser []
  (let [c (async/chan 100)
        i (atom 0)]
    (async/go-loop []
      (when-let [url-map (<! c)]
        (log/debug "[start-parser] received url:" (:url url-map))
        (swap! i inc)
        (recur)))
    {:chan c, :url-count i}))

(defn stop-parser [parser]
  (async/close! (:chan parser))
  (reset! (:url-count parser) 0))

(defstate parser
  :start (start-parser)
  :stop  (stop-parser parser))

(defn parse-handler
  "Sends urls to the parser to be parsed. Meant to be used with Itsy spider."
  [url-map]
  (log/debug "[parse-handler] sending url:" (:url url-map))
  (>!! (:chan parser) url-map)
  (log/debug "[parse-handler] url sent:" (:url url-map)))
