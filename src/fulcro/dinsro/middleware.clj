(ns dinsro.middleware
  (:require
   [buddy.core.bytes :as b]
   [clojure.core.async :as async]
   [com.fulcrologic.fulcro.server.api-middleware :as server]
   [com.fulcrologic.fulcro.networking.file-upload :as file-upload]
   [com.wsscode.pathom.connect :as pc]
   [com.wsscode.pathom.core :as p]
   [dinsro.config :refer [secret]]
   [dinsro.env :refer [defaults]]
   [dinsro.middleware.middleware :as middleware]
   [dinsro.resolvers :as dr]
   [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
   [ring.middleware.session.cookie :refer [cookie-store]]
   [taoensso.timbre :as timbre]))

(def pathom-endpoint "/pathom")

(def not-found-handler
  (fn [_req]
    {:status  404
     :headers {"Content-Type" "text/plain"}
     :body    "Not Found"}))

(def my-resolvers [dr/resolvers])

(def parser
  (p/parallel-parser
   {::p/env {::p/reader [p/map-reader
                         pc/parallel-reader
                         pc/open-ident-reader
                         p/env-placeholder-reader]
             ::pc/mutation-join-globals [:tempids]
             ::p/placeholder-prefixes #{">"}}
    ::p/mutate pc/mutate-async
    ::p/plugins [(pc/connect-plugin {::pc/register my-resolvers})
                 (p/post-process-parser-plugin p/elide-not-found)
                 p/error-handler-plugin
                 p/request-cache-plugin
                 p/trace-plugin]}))

(defn wrap-api
  [handler]
  (let [parser (fn [env query] (async/<!! (parser env query)))]
    (fn [{:keys [uri] :as request}]
      (if (= pathom-endpoint uri)
        (server/handle-api-request (:transit-params request) (partial parser {:request request}))
        (handler request)))))

(defn wrap-base [handler]
  (let [session-store (cookie-store {:key (b/slice secret 0 16)})]
    (-> ((:middleware defaults) handler)
        (wrap-api)
        (file-upload/wrap-mutation-file-uploads {})
        (server/wrap-transit-params)
        (server/wrap-transit-response)
        middleware/wrap-auth
        middleware/wrap-csrf
        (wrap-defaults
         (assoc-in site-defaults [:session :store] session-store))
        middleware/wrap-internal-error)))
