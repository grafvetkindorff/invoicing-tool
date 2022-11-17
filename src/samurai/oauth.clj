(ns samurai.oauth
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:import java.io.FileNotFoundException
           java.io.IOException
           java.io.InputStream
           java.io.File
           java.util.Properties
           org.apache.tika.Tika
           com.intuit.ipp.core.Context
           com.intuit.ipp.core.ServiceType
           com.intuit.ipp.exception.FMSException
           com.intuit.ipp.security.IAuthorizer
           com.intuit.ipp.security.OAuth2Authorizer
           com.intuit.ipp.security.OAuthAuthorizer
           com.intuit.ipp.util.Logger
           com.intuit.ipp.services.DataService
           com.intuit.ipp.data.BillPayment
           com.intuit.ipp.data.Bill
           com.intuit.ipp.services.QueryResult))

(defn load-properties
  []
  (let [prop (Properties.)
        propFileName (io/resource "config.properties")
        inputStream (io/input-stream propFileName)
        _ (doto prop
            (.load inputStream))]
    prop))

(defn get-context
  []
  (let [bearer-token "oauth2.accessToken"
        prop (load-properties)
        oauth (OAuth2Authorizer. (.getProperty prop bearer-token))
        company-id (.getProperty prop "company.id")]
    (Context. oauth ServiceType/QBO company-id)) )

;; String sql = "select * from account";
;; QueryResult queryResult = service.executeQuery(sql);
;; int count = queryResult.getEntities().size();

;; BillPayment account = BillHelper.getBillPaymentFields(service);
;; BillPayment savedBillPayment = service.add(account);

(defn do-sql-query
  [sql]
  (let [service (DataService. (get-context))]
    (try
      (.executeQuery service sql)
      (catch FMSException e
        (map #(.getMessage %) (.getErrorList e))))))

#_(map bean (.getEntities (do-sql-query "select * from payment")))


(defn get-bill-payment
  []
  (let [service (DataService. (get-context))]
    (.findAll service (Bill.))))

#_(get-bill-payment)


(def github-re-pattern #"Total .*")

(defn parse-invoice-file
  [path pattern]
  (let [invoice-file (File. path)
        parsed (str/split (.parseToString (Tika.) invoice-file) #"\n\n")]
   (filter some? (map #(re-matches pattern %) parsed))))


(comment
  (parse-invoice-file (str (System/getenv "HOME") "/tmp/github-invoice.pdf") github-re-pattern)

  )
