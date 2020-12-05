(ns dinsro.middleware
  (:require
   [buddy.core.bytes :as b]
   [clojure.core.async :as async]
   [com.fulcrologic.fulcro.server.api-middleware :as server]
   [com.fulcrologic.fulcro.networking.file-upload :as file-upload]
   [com.fulcrologic.rad.blob :as blob]
   [com.wsscode.pathom.connect :as pc]
   [com.wsscode.pathom.core :as p]
   [dinsro.components.blob-store :as bs]
   [dinsro.config :refer [secret]]
   [dinsro.env :refer [defaults]]
   [dinsro.middleware.middleware :as middleware]
   [dinsro.resolvers :as dr]
   [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
   [ring.middleware.session.cookie :refer [cookie-store]]
   [taoensso.timbre :as timbre]))

(def not-found-handler
  (fn [_req]
    {:status  404
     :headers {"Content-Type" "text/plain"}
     :body    "Not Found"}))

(def my-resolvers [dr/resolvers])

(def api-parser
  (p/parallel-parser
   {::p/env {::p/reader [p/map-reader pc/parallel-reader pc/open-ident-reader]
             ::pc/mutation-join-globals [:tempids]}
    ::p/mutate pc/mutate-async
    ::p/plugins [(pc/connect-plugin {::pc/register my-resolvers})
                 (p/post-process-parser-plugin p/elide-not-found)
                 p/error-handler-plugin]}))

(defn wrap-base [handler]
  (let [session-store (cookie-store {:key (b/slice secret 0 16)})]
    (-> ((:middleware defaults) handler)
        (server/wrap-api {:uri "/pathom"
                          :parser (fn [query] (async/<!! (api-parser {:foo :bar} query)))})
        (blob/wrap-blob-service "/images" bs/image-blob-store)
        (blob/wrap-blob-service "/files" bs/file-blob-store)
        (file-upload/wrap-mutation-file-uploads {})
        (server/wrap-transit-params)
        (server/wrap-transit-response)
        middleware/wrap-auth
        middleware/wrap-csrf
        (wrap-defaults
         (assoc-in site-defaults [:session :store] session-store))
        middleware/wrap-internal-error)))
