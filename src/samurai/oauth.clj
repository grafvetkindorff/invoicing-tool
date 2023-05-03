(ns samurai.oauth
  (:import [com.intuit.ipp.core Context ServiceType]
           [com.intuit.ipp.security OAuth2Authorizer]
           [com.intuit.ipp.services DataService]
           [com.intuit.oauth2.client OAuth2PlatformClient]
           com.intuit.oauth2.config.Environment
           com.intuit.oauth2.config.OAuth2Config$OAuth2ConfigBuilder))


(defonce qb-config {:client-id ""
                    :client-secret ""
                    :api-host "https://sandbox-quickbooks.api.intuit.com"
                    :redirect "http://localhost:8080/oauth2redirect"})


(defonce session-map (atom {}))


(defn get-oauth2-config
  [client-id client-secret]
  (-> (OAuth2Config$OAuth2ConfigBuilder. client-id client-secret)
                          (.callDiscoveryAPI Environment/SANDBOX)
                          .buildConfig))


(defn get-platform-client
  [oauth2-config]
  (OAuth2PlatformClient. oauth2-config))


(defn -make-data-service
  [realm-id access-token]
  (let [authorizer (OAuth2Authorizer. access-token)
        context (Context. authorizer ServiceType/QBO realm-id)]
    (DataService. context)))


(defn get-data-service
  []
  (-make-data-service (:realmid @session-map) (:access-token @session-map)))


(defonce oauth2-config (get-oauth2-config (:client-id qb-config) (:client-secret qb-config)))

(defonce platform-client (get-platform-client oauth2-config))


(defn refresh-token
  []
  (let [refresh-token-response (.refreshToken platform-client (:refresh-token @session-map))
        access-token (.getAccessToken refresh-token-response)
        refresh-token (.getRefreshToken refresh-token-response)]
    (swap! session-map
           assoc
           :access-token access-token
           :refresh-token refresh-token)
    [(:realmid @session-map) access-token]))


(comment)