(ns mail
  (:require [clojure-mail.core :as msg-core]
            [clojure-mail.message :as msg]
            [clojure.string :as str])
  (:import java.io.File
           java.io.FileOutputStream))


(defn make-messages-store
  [{:keys [protocol account password] :as _config}]
  (try
    (msg-core/store protocol account password)
    (catch Exception e
      (println "Error while connecting to the mail server: " e)
      '())))


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
  (def inbox-messages (msg-core/inbox (make-messages-store {:protocol "imap.gmail.com"
                                                            :account "avetkindorff@gmail.com"
                                                            :password "aepdqlqozzmexiph"
                                                            :interval 5000})))

  (def latest (msg/read-message (first inbox-messages)))

  (def content (->> (first inbox-messages)
                    read-attachments
                    (filter-by-content-type "APPLICATION/PDF")
                    first))

  )

