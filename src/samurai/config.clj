(ns samurai.config
  (:require [cprop.core :refer [load-config]]
            [integrant.core :as ig]))


(load-config)

(defonce services-configuration
  (ig/read-string (slurp "config.edn")))