(ns samurai.quickbooks-api
  (:require [samurai.oauth :refer [get-context]])
  (:import com.intuit.ipp.exception.FMSException))


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
