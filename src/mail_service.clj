(ns mail-service 
  (:require [clojure-mail.core :as msg-core]
            [clojure-mail.message :as msg]
            [mail :refer [make-messages-store]]))


(defn sync-messages
  "Sync messages from the inbox"
  [state {:keys [interval] :as config}]
  (println "Start syncing messages..." config @state interval)
  ;; TODO: save message store to atom
  (let [inbox-messages (msg-core/inbox (make-messages-store config))]

    (swap! core/ctx assoc :mail-service/inbox-messages inbox-messages)

    (println "syncing messages..." inbox-messages)
    (while (not= @state :halted)
      (when (and (not= @state :suspended) (seq inbox-messages))
        (msg/read-message (first inbox-messages)))
      (println "sleeping...")
      (Thread/sleep interval))))