(ns dinsro.middleware
  (:require
   [buddy.core.bytes :as b]
   [dinsro.config :refer [secret]]
   [dinsro.env :refer [defaults]]
   [dinsro.middleware.middleware :as middleware]
   [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
   [ring.middleware.session.cookie :refer [cookie-store]]
   [taoensso.timbre :as timbre]))

(def not-found-handler
  (fn [_req]
    {:status  404
     :headers {"Content-Type" "text/plain"}
     :body    "Not Found"}))

(defn wrap-base [handler]
  (-> ((:middleware defaults) handler)
      middleware/wrap-auth
      middleware/wrap-csrf
      (wrap-defaults
       (-> site-defaults
           (assoc-in [:security :anti-forgery] false)
           (assoc-in [:session :store] (cookie-store {:key (b/slice secret 0 16)}))))
      middleware/wrap-internal-error))
