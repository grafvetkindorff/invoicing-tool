(ns samurai.invoicing-tool
  (:require [integrant.core :as ig]
            [samurai.integrant :refer [halt-system! init-system]]))


(defn -main
  []
  (let [config (-> "config.edn" slurp ig/read-string)]
    (-> config
        init-system)))



(comment

  ;; Service to get the emails in the inbox
     ;; -- get the messages - how to sync the messages?
     ;; -- get the attachments - should we store the attachments?

  ;; Service to get the invoices from the QBO

  ;; Compare each message to the invoices and see if there is a match
  ;; attache matched invoice to the QBO entry via API

  ;; How to understand that this invoice is already attached to the QBO entry?

  (def system (-main))

  (halt-system! system)

  )