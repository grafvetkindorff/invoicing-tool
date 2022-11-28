(ns samurai.oauth
  (:require [samurai.common :refer [load-properties]])
  (:import com.intuit.ipp.core.Context
           com.intuit.ipp.core.ServiceType
           com.intuit.ipp.data.Bill
           com.intuit.ipp.data.BillPayment
           com.intuit.ipp.exception.FMSException
           com.intuit.ipp.security.IAuthorizer
           com.intuit.ipp.security.OAuth2Authorizer
           com.intuit.ipp.security.OAuthAuthorizer
           com.intuit.ipp.services.DataService
           com.intuit.ipp.services.QueryResult
           com.intuit.ipp.util.Logger
           com.sun.mail.util.BASE64DecoderStream
           java.io.File
           java.io.FileNotFoundException
           java.io.FileOutputStream
           java.io.InputStream
           java.io.IOException
           java.util.Properties
           org.apache.tika.Tika))


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

;; Get amounts
(map (comp :totalAmt bean) (.getEntities (do-sql-query "select * from payment")))


(defn get-bill-payment
  []
  (let [service (DataService. (get-context))]
    (.findAll service (Bill.))))

#_(get-bill-payment)

(comment)