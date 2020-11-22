(ns dinsro.middleware.middleware
  (:require
   [buddy.auth :refer [authenticated?]]
   [buddy.auth.accessrules :refer [restrict]]
   [buddy.auth.backends :as backends]
   [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
   [clojure.string :as str]
   [clojure.tools.logging :as log]
   [dinsro.config :refer [secret]]
   [dinsro.layout :refer [error-page]]
   [dinsro.middleware.formats :as formats]
   [hiccup.page :refer [html5]]
   [mount.core :refer [defstate]]
   [muuntaja.middleware :refer [wrap-format wrap-params]]
   [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
   [ring.util.response :as resp]
   [taoensso.timbre :as timbre]))

(defn index [csrf-token]
  (html5
   [:html {:lang "en"}
    [:head {:lang "en"}
     [:title "Application"]
     [:meta {:charset "utf-8"}]
     [:meta {:name "viewport" :content "width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"}]

     #_[:link {:href "https://cdn.jsdelivr.net/npm/fomantic-ui@2.7.8/dist/semantic.min.css"
               :rel  "stylesheet"}]
     [:link {:href "https://fonts.googleapis.com/css?family=Open+Sans"
             :rel  "stylesheet"}]
     [:link {:href "https://cdn.jsdelivr.net/webjars/org.webjars.npm/bulma/0.8.0/css/bulma.css"
             :rel  "stylesheet"}]
     [:link {:href "https://cdn.jsdelivr.net/webjars/org.webjars.npm/bulma-calendar/6.0.7/dist/css/bulma-calendar.min.css"
             :rel  "stylesheet"}]
     [:link {:rel "shortcut icon" :href "data:image/x-icon;," :type "image/x-icon"}]
     [:script {:src "https://use.fontawesome.com/releases/v5.3.1/js/all.js"
               :defer true}]
     [:script {:src "https://cdn.jsdelivr.net/webjars/org.webjars.npm/bulma-calendar/6.0.7/dist/js/bulma-calendar.js"}]
     [:link {:href "https://fonts.googleapis.com/icon?family=Material+Icons"
             :rel  "stylesheet"}]
     [:link {:href "https://code.getmdl.io/1.3.0/material.indigo-pink.min.css"
             :rel  "stylesheet"}]

     [:script (str "var fulcro_network_csrf_token = '" csrf-token "';")
      (str "var csrfToken = '" csrf-token "';")]]
    [:body
     [:div#app]
     [:script {:src "/js/main.js"}]
     [:link {:href "//cdnjs.cloudflare.com/ajax/libs/highlight.js/9.12.0/styles/github.min.css"
             :rel  "stylesheet"}]]]))

(defn wrap-internal-error [handler]
  (fn [req]
    (try
      (handler req)
      (catch Throwable t
        (log/error (.getCause t))
        (log/error t (.getMessage t))
        (error-page {:status 500
                     :title "Something very bad has happened!"
                     :message "We've dispatched a team of highly trained gnomes to take care of the problem."})))))

(defn wrap-html-routes [ring-handler]
  (fn [{:keys [uri anti-forgery-token] :as req}]
    (if (or (str/starts-with? uri "/api")
            (str/starts-with? uri "/images")
            (str/starts-with? uri "/files")
            (str/starts-with? uri "/js"))
      (ring-handler req)
      (resp/content-type
       (resp/response (index anti-forgery-token))
       "text/html"))))

(defn wrap-csrf [handler]
  (wrap-anti-forgery
   handler
   {:error-response
    (error-page
     {:status 403
      :title "Invalid anti-forgery token"})}))

(defn wrap-formats [handler]
  (let [wrapped (-> handler wrap-params (wrap-format formats/instance))]
    (fn [request]
      ;; disable wrap-formats for websockets
      ;; since they're not compatible with this middleware
      ((if (:websocket? request) handler wrapped) request))))

(defn on-error [request _response]
  {:status  403
   :headers {"Content-Type" "text/plain"}
   :body    (str "Access to " (:uri request) " is not authorized")})

(defn wrap-restricted [handler]
  (restrict handler {:handler (fn [request] (authenticated? request))
                     :on-error on-error}))

(defn wrap-restricted2
  [handler]
  (restrict handler {:handler (fn [request] (authenticated? request))
                     :on-error on-error}))

(defn users-authenticated?
  [request]
  (authenticated? request)
  false)

(def rules
  [{:uri "/api/v1/users" :handler users-authenticated?}])

(defstate ^{:on-reload :noop} token-backend
  :start
  (backends/jws {:secret secret}))

(defn wrap-auth [handler]
  (let [backend token-backend]
    (-> handler
        (wrap-authentication backend)
        (wrap-authorization backend))))

(def not-found-handler
  (fn [_req]
    {:status  404
     :headers {"Content-Type" "text/plain"}
     :body    "Not Found"}))
