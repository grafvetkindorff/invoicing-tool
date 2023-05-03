(ns samurai.invoicing-tool
  (:require [compojure.core :refer [ANY defroutes GET]]
            [hiccup.core :refer [html]]
            [integrant.core :as ig]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.params :refer [wrap-params]]
            [ring.util.response :refer [redirect]]
            [samurai.oauth :refer [oauth2-config platform-client qb-config
                                   session-map]]
            [samurai.quickbooks-api :refer [do-sql-query]])
  (:gen-class) 
  (:import [com.intuit.oauth2.config Scope]))


(defn page-index [_request]
  {:status 200
   :headers {"content-type" "text/html"}
   :body (html [:html
                [:head
                 [:title "Hello World"]]
                [:body
                 [:script "function qb_redirect() {
                             window.location.href = \"/connectToQuickbooks\";}"]
                 [:h1 "Hello World"]
                 [:p "This is a paragraph."]
                 [:button {:onclick "qb_redirect();"} "Authorize"]]])})


;; see next steps:
;; https://github.com/IntuitDeveloper/HelloWorld-Java/blob/master/src/main/java/com/intuit/developer/helloworld/controller/CallbackController.java
(defn page-hello [request]
  {:status 200
   :headers {"content-type" "text/plain"}
   :body "Hi there! Keep trying!"})


(defn oauth2-redirected
  [request]
  (let [code (get-in request [:query-params "code"])
        realmid (get-in request [:query-params "realmId"])
        bearer-token-response (.retrieveBearerTokens platform-client code (:redirect qb-config))
        access-token (.getAccessToken bearer-token-response)
        refresh-token (.getRefreshToken bearer-token-response)]
    (swap! session-map
           assoc
           :code code
           :realmid realmid
           :access-token access-token
           :refresh-token refresh-token))
  {:status 200
   :headers {"content-type" "text/plain"}
   :body (str "Connected with params: " @session-map
              "API-call result: " (map (comp :totalAmt bean)
                                       (.getEntities (do-sql-query "select * from payment"))))})


(defn page-404 [request]
  {:status 404
   :headers {"content-type" "text/plain"}
   :body "Page not found."})


(defn connect-to-quickbooks
  []
  (println "Connecting to Quickbooks...")
  (redirect (.prepareUrl
             oauth2-config
             (list Scope/Accounting)
             (:redirect qb-config)
             (.generateCSRFToken oauth2-config))))


(defroutes routes
  (GET "/" request (page-index request))
  (GET "/hello" request (page-hello request))
  (GET "/connectToQuickbooks" _ (connect-to-quickbooks))
  (ANY "/oauth2redirect" request (oauth2-redirected request))
  page-404)


(defmethod ig/init-key :app/server [_ opts]
  (jetty/run-jetty (wrap-params routes) (assoc opts :join? false)))


(defmethod ig/halt-key! :app/server [_ server]
  (.stop server))


(defn print-hello-world
  "Print 'Hello world' every interval milliseconds."
  [interval state]
  (while (not= @state :halted)
    (when (not= @state :suspended)
      (println "Hello world"))
    (Thread/sleep interval)))


;; Define a default method for keys without a specific implementation
(defmethod ig/init-key :default
  [_ value]
  value)


;; Define how to initialize the :app/hello-world-printer key
(defmethod ig/init-key :app/hello-world-printer
  [_ {:keys [interval]}]
  (let [state (atom :running)]
    {:service (future (print-hello-world interval state))
     :state-flag state}))


;; Define how to halt the :app/hello-world-printer key
(defmethod ig/halt-key! :app/hello-world-printer
  [_ {:keys [state-flag]}]
  (compare-and-set! state-flag :running :halted))


(defn init-system
  "Initialize the system from the configuration."
  [config]
  (ig/init config))


(defn halt-system!
  "Halt the given system."
  [system]
  (ig/halt! system))


;; Main function to start the application
(defn -main
  []
  (let [config (-> "config.edn" slurp ig/read-string)]
    (-> config
        init-system)))



(comment

  ;; Service to get the emails in the inbox
     ;; -- get the messages - how to sync the messages?
     ;; -- get the attachments - should we store the attachments?

  ;; Service to get the invoices from the QBO

  ;; Compare each message to the invoices and see if there is a match
  ;; attache matched invoice to the QBO entry via API

  ;; How to understand that this invoice is already attached to the QBO entry?

  (def system (-main))

  (halt-system! system)

  )