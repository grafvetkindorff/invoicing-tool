(ns web
  (:require [compojure.core :refer [ANY defroutes GET]]
            [hiccup.core :refer [html]]
            [oauth :refer [connect-to-quickbooks oauth2-redirected]]))


(defn page-index [_request]
  {:status 200
   :headers {"content-type" "text/html"}
   :body (html [:html
                [:head
                 [:title "QB start page"]]
                [:body
                 [:script "function qb_redirect() {
                             window.location.href = \"/connectToQuickbooks\";}"]
                 [:h1 "Hello World"]
                 [:p "This is a paragraph."]
                 [:button {:onclick "qb_redirect();"} "Authorize"]]])})


;; see next steps:
;; https://github.com/IntuitDeveloper/HelloWorld-Java/blob/master/src/main/java/com/intuit/developer/helloworld/controller/CallbackController.java
(defn page-hello [_request]
  {:status 200
   :headers {"content-type" "text/plain"}
   :body "Hi there! Keep trying!"})


(defn page-404 [_request]
  {:status 404
   :headers {"content-type" "text/plain"}
   :body "Page not found."})


(defroutes routes
  (GET "/" request (page-index request))
  (GET "/hello" request (page-hello request))
  (GET "/connectToQuickbooks" _ (connect-to-quickbooks))
  (ANY "/oauth2redirect" request (oauth2-redirected request))
  page-404)