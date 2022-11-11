(ns samurai.oauth
  (:require [clojure.java.io :as io])
  (:import java.io.FileNotFoundException
           java.io.IOException
           java.io.InputStream
           java.util.Properties
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
            (.load  inputStream))]
    prop))

(defn get-context
  []
  (let [bearer-token "oauth2.accessToken"
        prop (load-properties)
        oauth (OAuth2Authorizer. (.getProperty prop bearer-token))
        company-id (.getProperty prop "company-id")]
    (Context. oauth (ServiceType/QBO) company-id)) )

;; String sql = "select * from account";
;; QueryResult queryResult = service.executeQuery(sql);
;; int count = queryResult.getEntities().size();

;; BillPayment account = BillHelper.getBillPaymentFields(service);
;; BillPayment savedBillPayment = service.add(account);

(defn do-sql-query
  [sql]
  (let [service (DataService. (get-context))]
    (.executeQuery service sql)))

;; (do-sql-query "select * from companyinfo")

;; REST

(defn get-bill-payment
  []
  (let [service (DataService. (get-context))]
    (.findAll service (Bill.))))
