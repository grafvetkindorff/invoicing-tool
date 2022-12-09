(ns samurai.mail
  (:require [clojure-mail.core :as msg-core]
            [clojure-mail.message :as msg]
            [samurai.common :refer [load-properties]])
  (:import java.io.File
           java.io.FileNotFoundException
           java.io.FileOutputStream))


(defn make-store-from-properties
  []
  (let [prop (load-properties)]
                   (msg-core/store "imap.gmail.com"
                                   (.getProperty prop "email.account")
                                   (.getProperty prop "email.password"))))


(def inbox-messages (msg-core/inbox (make-store-from-properties)))

(def latest (msg/read-message (first inbox-messages)))


(defn get-messages
  [credentials]
  (msg-core/inbox credentials))


(defn get-msg-attachment
  [msg]
  (let [content (msg/get-content msg)]
    (map #(->> %
               (.getBodyPart content) ;; if type is com.sun.mail.imap.IMAPBodyPart -> parse recursevily
                                      ;; else get getDataHandler and parse file
               .getDataHandler
               .getName)
         (range (.getCount content)))))

(get-msg-attachment (first inbox-messages))

(-> (first inbox-messages)
    msg/get-content
    (.getBodyPart 0)
    type
    #_#_#_.getDataHandler
        .getContent
      .getCount
    #_msg/get-content
    #_#_(.getBodyPart 0)
      .getContentType)

(let [attachment-handler (-> inbox-messages
                             first
                             .getDataHandler
                             .getContent
                             (.getBodyPart 1)
                             .getDataHandler)
      out (FileOutputStream. (File. (str "/tmp/" (.getName attachment-handler))))]
  (.writeTo attachment-handler
            out)
  (.close out))

