(ns aristotl.db
  "Datomic bootstrap and Datomic + Pedestal interceptor"
  (:require [mount.core :refer [defstate]]
            [datomic.api :as d]
            [adzerk.env :as env]
            [io.pedestal.interceptor :refer [interceptor]]
            [io.rkn.conformity :as c]))


(env/def
  DATOMIC_URI (str "datomic:mem://" (d/squuid)))


(defn- new-connection []
  (d/create-database DATOMIC_URI)
  (d/connect DATOMIC_URI))

(defn- disconnect []
  true
  #_(d/delete-databbase DATOMIC_URI))

(defstate conn
  :start (new-connection)
  :stop (disconnect))



(defn bootstrap!
  "Bootstrap schema into the database."
  [conn]
  ;; TODO:     v Create resources/<your-schema.edn> and add "<your-schema>.edn" to this vector
  (doseq [rsc [ ]]
    (let [norms (c/read-resource rsc)]
      (c/ensure-conforms conn norms))))

 (def insert-datomic
   "Provide a Datomic conn and db in all incoming requests"
   (interceptor
     {:name ::insert-datomic
      :enter (fn [context]
               (-> context
                 (assoc-in [:request :conn] conn)
                 (assoc-in [:request :db] (d/db conn))))}))
