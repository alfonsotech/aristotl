(ns aristotl.db
  "Datomic bootstrap and Datomic + Pedestal interceptor"
  (:require [datomic.api :as d]
            [io.pedestal.interceptor :refer [interceptor]]
            [io.rkn.conformity :as c]))


(defonce uri (str "datomic:mem://" (d/squuid)))

(defn bootstrap!
  "Bootstrap schema into the database."
  [uri]
  (d/create-database uri)
  (let [conn (d/connect uri)]
    ;; TODO:     v Create resources/<your-schema.edn> and add "<your-schema>.edn" to this vector
    (doseq [rsc [ ]]
      (let [norms (c/read-resource rsc)]
        (c/ensure-conforms conn norms)))))

 (def insert-datomic
   "Provide a Datomic conn and db in all incoming requests"
   (interceptor
     {:name ::insert-datomic
      :enter (fn [context]
               (let [conn (d/connect uri)]
                 (-> context
                     (assoc-in [:request :conn] conn)
                     (assoc-in [:request :db] (d/db conn)))))}))

