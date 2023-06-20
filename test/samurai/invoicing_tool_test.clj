(ns samurai.invoicing-tool-test
  (:require [clojure.test :refer :all]
            [invoicing-tool :as tool]
            [matcho.core :as match]))

(deftest first-quickbooks-test
  (testing "we can call QuickBooks API from our application"
    (matcho/match (tool/api-call {:request "boo"})
                  {:status 200})))

;; expenses:
;; - expense is created by buy
;; - we receive pdf in email
;; - parse data
;;
;; incomes:
;; - we match outcoming google spreadsheet
