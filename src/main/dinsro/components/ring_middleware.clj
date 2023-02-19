(ns dinsro.components.ring-middleware
  (:require
   [buddy.core.bytes :as b]
   [clojure.data.json :as json]
   [clojure.string :as str]
   [cognitect.transit :as ct]
   [com.fulcrologic.fulcro.networking.file-upload :as file-upload]
   [com.fulcrologic.fulcro.networking.websockets :as fws]
   [com.fulcrologic.fulcro.server.api-middleware :as server]
   [com.fulcrologic.rad.blob :as blob]
   [compojure.core :refer [defroutes GET POST]]
   [dinsro.components.blob-store :as bs]
   [dinsro.components.config :as config :refer [secret]]
   [dinsro.components.parser :as parser]
   [dinsro.components.socket :as socket]
   [hiccup.page :refer [html5]]
   [lambdaisland.glogc :as log]
   [mount.core :refer [defstate]]
   [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
   [ring.middleware.session.cookie :refer [cookie-store]]
   [ring.util.response :as resp]
   [taoensso.sente.server-adapters.http-kit :refer [get-sch-adapter]]))

(def minimal false)

(defn index [csrf-token]
  (html5
   [:html {:lang "en"}
    [:head {:lang "en"}
     [:title "Application"]
     [:meta {:charset "utf-8"}]
     [:meta {:name "viewport" :content "width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no"}]
     [:script {:src
               (if minimal "https://cdn.jsdelivr.net/npm/jquery@3.3.1/dist/jquery.min.js"
                   "https://cdn.jsdelivr.net/npm/jquery@3.3.1/dist/jquery.js")}]
     [:link {:rel "stylesheet" :type "text/css" :href "/css/main.min.css"}]
     [:script {:src "/css/main.min.js"}]
     [:link {:rel "shortcut icon" :href "data:image/x-icon;," :type "image/x-icon"}]
     [:script (str "var fulcro_network_csrf_token = '" csrf-token "';")]]
    [:body {}
     [:div#sente-csrf-token {:data-csrf-token csrf-token}]
     [:div#app {}]
     [:script {:src "/js/main.js"}]]]))

(defn wrap-api [handler uri]
  (fn [request]
    (if (= uri (:uri request))
      (server/handle-api-request
       (:transit-params request)
       (fn [query]
         (parser/parser {:ring/request request} query)))
      (handler request))))

(def not-found-handler
  (fn [_req]
    {:status 404
     :body   {}}))

(defn wrap-html-routes [ring-handler]
  (fn [{:keys [uri anti-forgery-token] :as req}]
    (if (or (str/starts-with? uri "/api")
            (str/starts-with? uri "/css")
            (str/starts-with? uri "/images")
            (str/starts-with? uri "/files")
            (str/starts-with? uri "/js")
            (str/starts-with? uri "/.well-known"))
      (ring-handler req)
      (resp/content-type
       (resp/response (index anti-forgery-token))
       "text/html"))))

(defn nip05-response
  []
  (let [props
        {:names
         {"_"      "6fe701bde348f57e1068101830ad2015f32d3d51d0d685ff0f2812ee8635efec"
          "alice"  "efff8cd00d0fb7477935bfad061d549fc3f84ceec34646d7f526651aab47c00a"
          "bob"    "6bda57c3323ac4d8b4ca32729d07f1707b60df1c0625e7acab3cefefb001cf28"
          "dinsro" "6fe701bde348f57e1068101830ad2015f32d3d51d0d685ff0f2812ee8635efec"
          "duck"   "47b38f4d3721390d5b6bef78dae3f3e3888ecdbf1844fbb33b88721d366d5c88"}
         :relays
         {"6fe701bde348f57e1068101830ad2015f32d3d51d0d685ff0f2812ee8635efec"
          ["wss://relay.kronkltd.net"]

          "efff8cd00d0fb7477935bfad061d549fc3f84ceec34646d7f526651aab47c00a"
          ["wss://relay.kronkltd.net"]

          "6bda57c3323ac4d8b4ca32729d07f1707b60df1c0625e7acab3cefefb001cf28"
          ["wss://relay.kronkltd.net"]

          "47b38f4d3721390d5b6bef78dae3f3e3888ecdbf1844fbb33b88721d366d5c88"
          ["wss://relay.kronkltd.net"]}}]
    (json/json-str props)))

(defn wrap-well-known-routes
  [ring-handler]
  (fn [req]
    (let [{:keys [uri]} req]
      (if (str/starts-with? uri "/.well-known")
        (if (str/starts-with? uri "/.well-known/nostr.json")
          {:status 200 :body (nip05-response)}
          {:status 200 :body "Well known"})
        (ring-handler req)))))

(def transit-write-handlers
  {java.lang.Throwable
   (ct/write-handler
    "s"
    (fn [^java.lang.Throwable t]
      (str "EXCEPTION: " (.getMessage t))))

   java.time.Instant
   (ct/write-handler
    "t"
    (fn [^java.time.Instant inst]
      (.format
       (com.cognitect.transit.impl.AbstractParser/getDateTimeFormat)
       (java.util.Date/from inst))))})

(defn query-parser
  [_env query]
  (log/info :query-parser/starting {:query query}))

(defstate websockets
  :start
  (fws/start!
   (fws/make-websockets
    parser/parser
    {:http-server-adapter (get-sch-adapter)
     :parser-accepts-env? true
     ;; I'm not going to cover how to handle CSRF well, if this is a toy project
     ;;  this bit doesn't matter as much, if it isn't please sit down and read about it.
     ;;  I've added some notes at the end.
     :sente-options       {:csrf-token-fn nil}}))

  :stop
  (fws/stop! websockets))

(defn wrap-websockets [handler]
  (fws/wrap-api handler websockets))

(defroutes base-app
  (GET "/.well-known/nostr.json" []
    (resp/content-type
     (resp/response (nip05-response))
     "application/json"))
  (GET  "/chsk" req
    (socket/ring-ajax-get-or-ws-handshake req))
  (POST "/chsk" req
    (socket/ring-ajax-post req))
  (GET "/" {:keys [anti-forgery-token]}
    (resp/content-type
     (resp/response (index anti-forgery-token))
     "text/html")))
(defstate middleware
  :start
  (let [defaults-config (:ring.middleware/defaults-config config/config {})
        session-store   (cookie-store {:key (b/slice secret 0 16)})
        defaults-config (assoc-in defaults-config [:session :store] session-store)
        defaults-config (merge site-defaults defaults-config)]
    (-> base-app
        (wrap-api "/api")
        (wrap-websockets)
        (file-upload/wrap-mutation-file-uploads {})
        (blob/wrap-blob-service "/images" bs/image-blob-store)
        (blob/wrap-blob-service "/files" bs/file-blob-store)
        (server/wrap-transit-params {})
        (server/wrap-transit-response {:opts {:handlers transit-write-handlers}})
        (wrap-html-routes)
        (wrap-defaults defaults-config))))
