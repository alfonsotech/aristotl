(ns aristotl.test-helpers
  (:require [datomic.api :as d]
            [io.pedestal.http :as http]
            [io.pedestal.test :refer [response-for]]
            [cheshire.core :as json]
            [aristotl.database :as db]))

(defonce ^:private service-fn (atom nil))

#_(defn test-service
      "Return a service-fn for use with Pedestal's `response-for` test helper."
      []
      (db/bootstrap! db/uri)
      (swap! service-fn #(or % (::http/service-fn (http/create-servlet aristotl/service)))))

#_(defn with-seeds
  "Return a db with seed tx-data applied"
  ([tx-data] (with-seeds (d/db (d/connect db/uri)) tx-data))
  ([db tx-data]
   (:db-after (d/with db tx-data))))
