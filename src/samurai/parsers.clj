(ns samurai.parsers
  (:require [clojure.string :as str]))


(def github-re-pattern #"Total .*")
#_(def pdf-attachment-re-pattern #"APPLICATION/PDF; .*")


(defn parse-invoice-file
  [path pattern]
  (let [invoice-file (File. path)
        parsed (str/split (.parseToString (Tika.) invoice-file) #"\n\n")]
   (filter some? (map #(re-matches pattern %) parsed))))


(comment

  (parse-invoice-file "/tmp/invoice_2359483.pdf" github-re-pattern)

  )