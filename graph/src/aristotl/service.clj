(ns aristotl.service
  (:require [mount.core :refer [defstate]]
            [io.pedestal.http :as http]
            [io.pedestal.http.route.definition :refer [defroutes]]
            [clojure.tools.logging :as log]
            [aristotl.routes :refer [routes]]
            [aristotl.db :as db]
            [adzerk.env :as env]))

;; Dev environment stuff. This will be overridden on prod
(env/def
  HOST "0.0.0.0"
  PORT "8080")

(def api-service
  {::http/host HOST
   ::http/port (Integer/parseInt PORT)
   ::http/type :jetty
   ::http/join? false
   ::http/resource-path "/public"
   ::http/routes routes})

(defn start-pedestal [config]
  (let [svc (http/create-server config)]
    (log/info "Starting Pedestal")
    ;;(db/bootstrap! db/uri)
    (http/start svc)))

(defn stop-pedestal []
  (http/stop))

(defstate pedestal
  :start start-pedestal
  :stop stop-pedestal)
