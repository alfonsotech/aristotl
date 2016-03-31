(ns aristotl.spider
  "The Spider component crawls encyclopedia pages. The custom handler
  stores the content into ElasticSearch and the metadata into
  Datomic."
  (:require [mount.core :refer [defstate]]
            [clojure.tools.logging :as log]
            [adzerk.env :as env]
            [itsy.core :as itsy]
            [itsy.handlers.elasticsearch :refer [make-es-handler]]
            [net.cgrand.enlive-html :as html]
            [clojure.string :as str]))

(env/def
  ES_URL "http://localhost:9200/")

(defn log-handler [{:keys [url body] :as m}]
  (log/info "Crawling" url))

(defn warn-handler [{:keys [url body] :as m}]
  (if (= (count body) 0)
    (log/warn "Itsy found no links on" url)))


(def sources
  "Schema: {<3-letter name> <itsy crawl settings>}"
  {:sep {:url "http://plato.stanford.edu/contents.html"
         :handler log-handler
         :workers 5
         :url-limit 10
         :url-extractor itsy/extract-all ;; FIXME: study itsy/extract-all and make my own implementation ??
         :http-opts {}
         :host-limit true
         :polite? true}})

(defstate crawler
  :start (itsy/crawl (:sep sources))
  :stop (itsy/stop-workers crawler))
