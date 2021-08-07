(ns dinsro.components.ring-middleware
  (:require
   [clojure.string :as str]
   [dinsro.components.blob-store :as bs]
   [dinsro.components.config :as config]
   [dinsro.components.parser :as parser]
   [com.fulcrologic.fulcro.networking.file-upload :as file-upload]
   [com.fulcrologic.fulcro.server.api-middleware :as server]
   [com.fulcrologic.rad.blob :as blob]
   [hiccup.page :refer [html5]]
   [mount.core :refer [defstate]]
   [ring.middleware.defaults :refer [wrap-defaults]]
   [ring.util.response :as resp]
   [taoensso.timbre :as log]))

(defn index [csrf-token]
  (html5
   [:html {:lang "en"}
    [:head {:lang "en"}
     [:title "Application"]
     [:meta {:charset "utf-8"}]
     [:meta {:name "viewport" :content "width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"}]

     [:link {:href "https://cdnjs.cloudflare.com/ajax/libs/bulma/0.9.1/css/bulma.min.css"
             :rel  "stylesheet"}]
     [:link {:href "https://cdn.jsdelivr.net/npm/fomantic-ui@2.7.8/dist/semantic.min.css"
             :rel  "stylesheet"}]

     ;; [:link {:href "https://fonts.googleapis.com/icon?family=Material+Icons"
     ;;         :rel  "stylesheet"}]

     [:link {:rel "shortcut icon" :href "data:image/x-icon;," :type "image/x-icon"}]
     ;; [:script {:src "https://use.fontawesome.com/releases/v5.3.1/js/all.js"}]
     [:script (str "var fulcro_network_csrf_token = '" csrf-token "';")]]
    [:body
     [:div#app {:style "height: 100%"}]
     [:script {:src "/js/main.js"}]]]))

(defn wrap-api [handler uri]
  (fn [request]
    (if (= uri (:uri request))
      (server/handle-api-request (:transit-params request)
                                 (fn [query]
                                   (parser/parser {:ring/request request}
                                                  query)))
      (handler request))))

(def not-found-handler
  (fn [_req]
    {:status 404
     :body   {}}))

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

(defstate middleware
  :start
  (let [defaults-config (:ring.middleware/defaults-config config/config)]
    (-> not-found-handler
        (wrap-api "/api")
        (file-upload/wrap-mutation-file-uploads {})
        (blob/wrap-blob-service "/images" bs/image-blob-store)
        (blob/wrap-blob-service "/files" bs/file-blob-store)
        (server/wrap-transit-params {})
        (server/wrap-transit-response {})
        (wrap-html-routes)
        (wrap-defaults defaults-config))))