(ns samurai.mail-service 
  (:require [clojure-mail.message :as msg]
            [samurai.mail :refer [make-messages-store]]))


(defn sync-messages
  "Sync messages from the inbox"
  [state {:keys [interval] :as config}]
  (println "Start syncing messages...")
  (let [inbox-messages (make-messages-store config)]
    (while (not= @state :halted)
     (println "syncing messages...")
     (when (not= @state :suspended)
       (msg/read-message (first inbox-messages)))
     (Thread/sleep interval))))