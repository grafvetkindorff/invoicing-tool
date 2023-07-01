(ns core
  (:gen-class) 
  (:require [integrant :refer [init-system]]
            [integrant.core :as ig]))


(def ctx
  (atom {:mail-service/some-var 123
         :oauth/some-var 456}))


(defn -main
  []
  (let [config (-> "config.edn" slurp ig/read-string)]
    (-> config
        init-system)))



(comment

  ;; Service to get the emails in the inbox
     ;; -- get the messages - how to sync the messages?
     ;; -- get the attachments - should we store the attachments?

  ;; Compare each message to the invoices and see if there is a match
  ;; attache matched invoice to the QBO entry via API

  ;; How to understand that this invoice is already attached to the QBO entry?

  ;; Store auth session in the database

  (require 'integrant)

  (def system (-main))

  (integrant/halt-system! system)

  )
