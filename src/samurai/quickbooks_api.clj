(ns samurai.quickbooks-api
  (:require [samurai.oauth :refer [get-data-service]])
  (:import [com.intuit.ipp.data Bill]
           com.intuit.ipp.exception.FMSException))


;; String sql = "select * from account";
;; QueryResult queryResult = service.executeQuery(sql);
;; int count = queryResult.getEntities().size();

;; BillPayment account = BillHelper.getBillPaymentFields(service);
;; BillPayment savedBillPayment = service.add(account);

(defn do-sql-query
  [sql]
  (let [service (get-data-service)]
    (try
      (.executeQuery service sql)
      ;; TODO: refresh token if expired
      (catch FMSException e
        (map #(.getMessage %) (.getErrorList e))))))


(defn get-bill-payment
  []
  (let [service (get-data-service)]
    (.findAll service (Bill.))))


(comment
  (get-bill-payment)

  ;; Get amounts
  (map (comp :totalAmt bean) (.getEntities (do-sql-query "select * from payment")))

  )
