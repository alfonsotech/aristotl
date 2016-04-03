(ns aristotl.spider
  "The Spider component crawls encyclopedia pages. The custom handler
  stores the content into ElasticSearch and the metadata into
  Datomic."
  (:require [clojure.tools.logging :as log]
            [mount.core :refer [defstate]]
            [adzerk.env :as env]
            [itsy.core :as itsy]
            [aristotl.parse :as parse]))

(env/def
  ES_URL "http://localhost:9200/")

(defn log-handler [{:keys [url body] :as m}]
  (log/debug "Crawling" url))

(def handler
  (comp parse/parse-handler log-handler))

(def sources
  "Schema: {<3-letter name> <itsy crawl settings>}"
  {:sep {:url           "http://plato.stanford.edu/contents.html"
         :handler       log-handler
         :workers       5
         :url-limit     -1
         :url-extractor itsy/extract-all ;; FIXME: study itsy/extract-all and make my own implementation ??
         :http-opts     {}
         :host-limit    true
         :polite?       true}})

(defstate crawler
  :start (itsy/crawl (:sep sources))
  :stop  (itsy/stop-workers crawler))
