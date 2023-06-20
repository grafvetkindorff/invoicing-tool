(ns integrant
  (:require [integrant.core :as ig]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.params :refer [wrap-params]]
            [mail-service :refer [sync-messages]]
            [oauth :refer [get-oauth2-config get-platform-client
                                   qb-config]]
            [web :refer [routes]]))


(defmethod ig/init-key :app/server [_ opts]
  (jetty/run-jetty (wrap-params routes) (assoc opts :join? false)))


(defmethod ig/halt-key! :app/server [_ server]
  (.stop server))


(defmethod ig/init-key :default
  [_ value]
  value)


(defmethod ig/init-key :app/email-service
  [_ config]
  (let [state (atom :running)]
    {:service (future (sync-messages state config))
     :state-flag state}))


(defmethod ig/halt-key! :app/email-service
  [_ {:keys [state-flag]}]
  (compare-and-set! state-flag :running :halted))


(defmethod ig/init-key :app/qb-client
  [_ config]
  (println "Initializing QB client..." config)
  (let [oauth2-config (get-oauth2-config (:auth config))
        platform-client (get-platform-client oauth2-config)]
    (reset! qb-config (merge
                       {:oauth2-config oauth2-config
                        :platform-client platform-client}
                       config))))


(defmethod ig/halt-key! :app/qb-client
  [_ _]
  (reset! qb-config nil))


(defn init-system
  "Initialize the system from the configuration."
  [config]
  (ig/init config))


(defn halt-system!
  "Halt the given system."
  [system]
  (ig/halt! system))

