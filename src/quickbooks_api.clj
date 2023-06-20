(ns quickbooks-api
  (:require [oauth :refer [get-data-service refresh-token]])
  (:import [com.intuit.ipp.data Bill]
           [com.intuit.ipp.exception FMSException InvalidTokenException]))


;; BillPayment account = BillHelper.getBillPaymentFields(service);
;; BillPayment savedBillPayment = service.add(account);

;; TODO: implement retrying logic
(defn do-sql-query
  [sql]
  (try
    (-> (get-data-service)
        (.executeQuery sql))
    (catch InvalidTokenException e
      (refresh-token)
      {:errors (map #(str (.getMessage %) ": " (.getDetail %)) (.getErrorList e))})
    (catch FMSException e
      {:errors (map #(str (.getMessage %) ": " (.getDetail %)) (.getErrorList e))})))


(defn get-bill-payment
  []
  (let [service (get-data-service)]
    (.findAll service (Bill.))))


(comment
  (get-bill-payment)

  ;; Get amounts
  (map (comp :totalAmt bean) (.getEntities (do-sql-query "select * from payment")))

  (def result (do-sql-query "select * from payment"))

  (.size (.getEntities result))

  )
