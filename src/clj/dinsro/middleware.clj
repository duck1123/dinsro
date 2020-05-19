(ns dinsro.middleware
  (:require
   [buddy.auth :refer [authenticated?]]
   [buddy.auth.accessrules :refer [restrict]]
   [buddy.auth.backends :as backends]
   [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
   [buddy.core.bytes :as b]
   [dinsro.env :refer [defaults]]
   [clojure.tools.logging :as log]
   [dinsro.config :refer [secret]]
   [dinsro.layout :refer [error-page]]
   [dinsro.middleware.formats :as formats]
   [mount.core :refer [defstate]]
   [muuntaja.middleware :refer [wrap-format wrap-params]]
   [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
   [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
   [ring.middleware.session.cookie :refer [cookie-store]]
   [taoensso.timbre :as timbre]))

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

(defstate ^{:on-reload :noop} token-backend
  :start
  (backends/jws {:secret secret}))

(defn users-authenticated?
  [request]
  (authenticated? request)
  false)

(def rules
  [{:uri "/api/v1/users" :handler users-authenticated?}])

(defn wrap-auth [handler]
  (let [backend token-backend]
    (-> handler
        ;; (wrap-access-rules {:rules rules :on-error on-error})
        (wrap-authentication backend)
        (wrap-authorization backend))))

(defn wrap-base [handler]
  (-> ((:middleware defaults) handler)
      wrap-auth
      (wrap-defaults
       (-> site-defaults
           (assoc-in [:security :anti-forgery] false)
           (assoc-in [:session :store] (cookie-store {:key (b/slice secret 0 16)}))))
      wrap-internal-error))
