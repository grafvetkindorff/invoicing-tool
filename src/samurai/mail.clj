(ns samurai.mail
  (:require [clojure-mail.core :as msg-core]
            [clojure-mail.message :as msg]
            [samurai.common :refer [load-properties]]
            [clojure.string :as str])
  (:import java.io.File
           java.io.FileOutputStream))


(defn make-store-from-properties
  []
  (let [prop (load-properties)]
    (msg-core/store "imap.gmail.com"
                    (.getProperty prop "email.account")
                    (.getProperty prop "email.password"))))


(defn get-messages
  [credentials]
  (msg-core/inbox credentials))


(defn- read-attachments*
  [mime-multi-part]
  (let [count (.getCount mime-multi-part)]
    (for [part (map #(.getBodyPart mime-multi-part %) (range count))]
      (if (.startsWith (msg/content-type part) "multipart")
        (read-attachments* (.getContent part))
        (let [data-handler (.getDataHandler part)]
          {:filename (.getName data-handler)
           :content-type (.getContentType part)
           :data-handler data-handler})))))


(defn read-attachments
  [message]
  (read-attachments* (.getContent message)))


(defn filter-by-content-type
  [content-type attachments]
  (filter #(str/starts-with? (:content-type %) content-type) attachments))


(defn store-attachment
  [path attachment]
  (let [out (FileOutputStream. (File. path))]
    (.writeTo (:data-handler attachment)
              out)
    (.close out)))


(comment
  (def inbox-messages (msg-core/inbox (make-store-from-properties)))

  (def latest (msg/read-message (first inbox-messages)))

  (def content (->> (first inbox-messages)
                    read-attachments
                    (filter-by-content-type "APPLICATION/PDF")
                    first))

  )

