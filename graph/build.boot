(set-env! :source-paths   #{"src" "test"}
          :resource-paths #{"resources" "config"}
          :dependencies   '[[org.clojure/clojure "1.8.0"]
                            [org.clojure/core.typed "0.3.22"]
                            [org.clojure/core.match "0.3.0-alpha4"]
                            [org.clojure/tools.logging "0.3.1"]
                            [org.clojure/core.async "0.2.374"]
                            [adzerk/boot-test "1.1.1" :scope "test"]
                            [robert/hooke "1.3.0"]

                            [mount "0.1.10"]
                            [io.pedestal/pedestal.service "0.4.1"]
                            [io.pedestal/pedestal.jetty   "0.4.1"]

                            [itsy "0.1.1"
                             :exclusions [org.slf4j/slf4j-log4j12]]
                            [clj-http   "2.1.0"]
                            [adzerk/env "0.3.0"]
                            [enlive     "1.1.6"]

                            ;; ElasticSearch
                            [org.elasticsearch/elasticsearch "2.3.0"
                                   :exclusions [org.antlr/antlr-runtime
                                                org.apache.lucene/lucene-analyzers-common
                                                org.apache.lucene/lucene-grouping
                                                org.apache.lucene/lucene-highlighter
                                                org.apache.lucene/lucene-join
                                                org.apache.lucene/lucene-memory
                                                org.apache.lucene/lucene-misc
                                                org.apache.lucene/lucene-queries
                                                org.apache.lucene/lucene-queryparser
                                                org.apache.lucene/lucene-sandbox
                                                org.apache.lucene/lucene-spatial
                                                org.apache.lucene/lucene-suggest
                                                org.ow2.asm/asm
                                                org.ow2.asm/asm-commons]]

                            ;; Datomic
                            [com.datomic/datomic-free "0.9.5350"
                             :exclusions [joda-time
                                          org.slf4j/slf4j-nop
                                          org.slf4j/slf4j-log4j12]]
                            [io.rkn/conformity "0.4.0"
                             :exclusions [com.datomic/datomic-free]]

                            ;; Logging
                            [ch.qos.logback/logback-classic "1.1.7"
                             :exclusions [org.slf4j/slf4j-api]]
                            [org.slf4j/jul-to-slf4j "1.7.20"]
                            [org.slf4j/jcl-over-slf4j "1.7.20"]
                            [org.slf4j/log4j-over-slf4j "1.7.20"]])

(def +version+ "0.0.1-SNAPSHOT")
(task-options! pom {:project 'aristotl
                    :version (str +version+ "-standalone")
                    :description "FIXME: write description"
                    :license {"MIT" "See LICENSE.txt"}})



(require 'boot.repl
         '[clojure.tools.namespace.repl :refer [refresh]])
(swap! boot.repl/*default-dependencies*
       concat '[[cider/cider-nrepl "0.8.2"]])


(swap! boot.repl/*default-middleware*
       conj 'cider.nrepl/cider-middleware)


(load-data-readers!)

(deftask bootstrap
  "Bootstrap the Datomic database"
  []
  (require '[aristotl.db :as db])
  ((resolve 'db/bootstrap!) @(resolve 'aristotl.db/uri)))


(require '[clojure.tools.namespace.repl :refer [set-refresh-dirs]])

(deftask dev []
  (set-env! :source-paths #(conj % "dev"))

  (apply set-refresh-dirs (get-env :directories))
  (load-data-readers!) ;; for datomic

  (require 'dev)
  (in-ns 'dev))


(deftask build
  "Build my project."
  []
  (comp
   (aot :namespace '#{aristotl})
   (pom)
   (uber)
   (jar :main 'aristotl)))
