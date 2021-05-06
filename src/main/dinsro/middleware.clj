(ns dinsro.middleware
  (:require
   [buddy.auth.backends :as backends]
   [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
   [buddy.core.bytes :as b]
   [clojure.core.async :as async]
   [clojure.tools.logging :as log]
   [com.fulcrologic.fulcro.server.api-middleware :as server]
   [com.wsscode.pathom.connect :as pc]
   [com.wsscode.pathom.core :as p]
   [dinsro.config :refer [secret]]
   [dinsro.env :refer [defaults]]
   [dinsro.layout :refer [error-page]]
   [dinsro.resolvers :as dr]
   [mount.core :refer [defstate]]
   [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
   [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
   [ring.middleware.session.cookie :refer [cookie-store]]
   [taoensso.timbre :as timbre]))

(def pathom-endpoint "/pathom")

(def my-resolvers [dr/resolvers])

(defn wrap-internal-error [handler]
  (fn [req]
    (try
      (handler req)
      (catch Throwable t
        (log/error (.getCause t))
        (log/error t (.getMessage t))
        (error-page {:status  500
                     :title   "Something very bad has happened!"
                     :message "We've dispatched a team of highly trained gnomes to take care of the problem."})))))

(defn wrap-csrf [handler]
  (wrap-anti-forgery
   handler
   {:error-response
    (error-page
     {:status 403
      :title  "Invalid anti-forgery token"})}))

(defstate ^{:on-reload :noop} token-backend
  :start
  (backends/jws {:secret secret}))

(defn wrap-auth [handler]
  (let [backend token-backend]
    (-> handler
        (wrap-authentication backend)
        (wrap-authorization backend))))

(def parser
  (p/parallel-parser
   {::p/env     {::p/reader                 [p/map-reader
                                             pc/parallel-reader
                                             pc/open-ident-reader
                                             p/env-placeholder-reader]
                 ::pc/mutation-join-globals [:tempids]
                 ::p/placeholder-prefixes   #{">"}}
    ::p/mutate  pc/mutate-async
    ::p/plugins [(pc/connect-plugin {::pc/register my-resolvers})
                 (p/post-process-parser-plugin p/elide-not-found)
                 p/error-handler-plugin
                 ;; p/request-cache-plugin
                 p/trace-plugin]}))

(defn wrap-api
  [handler]
  (let [parser (fn [env query] (async/<!! (parser env (timbre/spy query))))]
    (fn [{:keys [uri] :as request}]
      (if (= pathom-endpoint uri)
        (server/handle-api-request
         (:transit-params request)
         (partial parser {:request request}))
        (handler request)))))

(defn wrap-base [handler]
  (let [session-store (cookie-store {:key (b/slice secret 0 16)})]
    (-> ((:middleware defaults) handler)
        (wrap-api)
        (server/wrap-transit-params)
        (server/wrap-transit-response)
        wrap-auth
        wrap-csrf
        (wrap-defaults
         (assoc-in site-defaults [:session :store] session-store))
        wrap-internal-error)))
