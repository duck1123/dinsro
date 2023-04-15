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
   [dinsro.components.config :as c.config]
   [dinsro.components.parser :as c.parser]
   [dinsro.components.socket :as c.socket]
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
     [:link {:rel "stylesheet" :type "text/css" :href "/css/semantic.min.css"}]
     [:script {:src "/css/semantic.min.js"}]
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
         (@c.parser/parser {:ring/request request} query)))
      (handler request))))

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

(declare websockets)

(defn start-websockets!
  []
  (log/info :start-websockets!/starting {})
  (let [parser     @c.parser/parser
        token-fn   nil
        ws-options {:http-server-adapter (get-sch-adapter)
                    :parser-accepts-env? true
                    :sente-options       {:csrf-token-fn token-fn}}
        config     (fws/make-websockets parser ws-options)
        response   (fws/start! config)]
    (log/trace :start-websockets!/finished {:response response})
    response))

(defn stop-websockets!
  []
  (log/info :stop-websockets!/starting {})
  (let [response (fws/stop! @websockets)]
    (log/trace :stop-websockets!/finished {:response response})
    response))

(defstate websockets
  :start (start-websockets!)
  :stop (stop-websockets!))

(defn wrap-websockets [handler]
  (fws/wrap-api handler websockets))

(defroutes base-app
  (GET "/.well-known/nostr.json" []
    (resp/content-type
     (resp/response (nip05-response))
     "application/json"))
  (GET  "/chsk" req
    (c.socket/ring-ajax-get-or-ws-handshake req))
  (POST "/chsk" req
    (c.socket/ring-ajax-post req))
  (GET "/" {:keys [anti-forgery-token]}
    (resp/content-type
     (resp/response (index anti-forgery-token))
     "text/html")))

(defn get-session-store
  []
  (log/info :get-session-store/starting {})
  (let [store (cookie-store {:key (b/slice @c.config/secret 0 16)})]
    (log/info :get-session-store/finished {:store store})
    store))

(defn get-middleware-config
  []
  (log/info :get-middleware-config/starting {})
  (let [defaults-config (:ring.middleware/defaults-config (c.config/get-config) {})
        session-store   (get-session-store)
        defaults-config (assoc-in defaults-config [:session :store] session-store)
        defaults-config (merge site-defaults defaults-config)]
    (log/info :get-middleware-config/finished {:defaults-config defaults-config})
    defaults-config))

(defn start-middleware!
  []
  (log/info :start-middleware!/starting {})
  (let [defaults-config (get-middleware-config)]
    (log/info :start-middleware!/config {:defaults-config defaults-config})
    (let [middleware (-> base-app
                         (wrap-api "/api")
                         (wrap-websockets)
                         (file-upload/wrap-mutation-file-uploads {})
                         (blob/wrap-blob-service "/images" @bs/image-blob-store)
                         (blob/wrap-blob-service "/files" @bs/file-blob-store)
                         (server/wrap-transit-params {})
                         (server/wrap-transit-response {:opts {:handlers transit-write-handlers}})
                         (wrap-html-routes)
                         (wrap-defaults defaults-config))]
      (log/info :start-middleware!/finished {:middleware middleware})
      middleware)))

(defstate middleware
  :start (start-middleware!))
