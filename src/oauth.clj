(ns oauth
  (:require [ring.util.response :refer [redirect]]) 
  (:import [com.intuit.ipp.core Context ServiceType]
           [com.intuit.ipp.security OAuth2Authorizer]
           [com.intuit.ipp.services DataService]
           [com.intuit.oauth2.client OAuth2PlatformClient]
           [com.intuit.oauth2.config Scope]
           com.intuit.oauth2.config.Environment
           com.intuit.oauth2.config.OAuth2Config$OAuth2ConfigBuilder))


;; TODO: maybe merge into one atom?
;; TODO: store session in the database
(defonce qb-config (atom {}))
(defonce session-map (atom {}))

{:qb-client {}
 :sessions {}
 :mail-service {}}


(defn get-redirect
  []
  (get-in @qb-config [:auth :redirect]))


(defn get-oauth2-config
  [{:keys [client-id client-secret environment-type]}]
  (-> (OAuth2Config$OAuth2ConfigBuilder. client-id client-secret)
      (.callDiscoveryAPI (or environment-type Environment/SANDBOX))
      .buildConfig))


(defn get-platform-client
  [oauth2-config]
  (OAuth2PlatformClient. oauth2-config))


(defn -make-data-service
  [{:keys [realm-id access-token]}]
  (println "Making data service..." realm-id access-token)
  (let [authorizer (OAuth2Authorizer. access-token)
        context (Context. authorizer ServiceType/QBO realm-id)]
    (DataService. context)))


(defn get-data-service
  []
  (-make-data-service @session-map))


(defn oauth2-redirected
  [request]
  (let [code (get-in request [:query-params "code"])
        realmid (get-in request [:query-params "realmId"])
        bearer-token-response (.retrieveBearerTokens
                               (:platform-client @qb-config) code (get-redirect))
        access-token (.getAccessToken bearer-token-response)
        refresh-token (.getRefreshToken bearer-token-response)]
    (swap! session-map
           assoc
           :code code
           :realm-id realmid
           :access-token access-token
           :refresh-token refresh-token))
  {:status 200
   :headers {"content-type" "text/plain"}
   :body "connected"})


(defn connect-to-quickbooks
  []
  (redirect (.prepareUrl
             (:oauth2-config @qb-config)
             (list Scope/Accounting)
             (get-redirect)
             (.generateCSRFToken (:oauth2-config @qb-config)))))


(defn refresh-token
  []
  (let [refresh-token-response (.refreshToken (:platform-client @qb-config)
                                              (:refresh-token @session-map))
        access-token (.getAccessToken refresh-token-response)
        refresh-token (.getRefreshToken refresh-token-response)]
    (swap! session-map
           assoc
           :access-token access-token
           :refresh-token refresh-token)
    [(:realm-id @session-map) access-token]))



(comment

  @qb-config

  @session-map

  )