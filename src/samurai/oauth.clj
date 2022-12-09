(ns samurai.oauth
  (:require [samurai.common :refer [load-properties]])
  (:import com.intuit.ipp.core.Context
           com.intuit.ipp.core.ServiceType
           com.intuit.ipp.data.Bill
           com.intuit.ipp.data.BillPayment
           com.intuit.ipp.security.IAuthorizer
           com.intuit.ipp.security.OAuth2Authorizer
           com.intuit.ipp.security.OAuthAuthorizer
           com.intuit.ipp.services.DataService
           com.intuit.ipp.services.QueryResult
           com.intuit.ipp.util.Logger
           com.sun.mail.util.BASE64DecoderStream
           java.io.InputStream
           java.io.IOException
           org.apache.tika.Tika))


(defn get-context
  []
  (let [bearer-token "oauth2.accessToken"
        prop (load-properties)
        oauth (OAuth2Authorizer. (.getProperty prop bearer-token))
        company-id (.getProperty prop "company.id")]
    (Context. oauth ServiceType/QBO company-id)) )


(comment)