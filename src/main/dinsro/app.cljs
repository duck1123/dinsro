(ns dinsro.app
  (:require
   [com.fulcrologic.fulcro.application :as app]
   [com.fulcrologic.fulcro.networking.http-remote :as http]
   [taoensso.timbre :as log]))

(defonce app
  (app/fulcro-app
   {:remotes
    {:remote
     (http/fulcro-http-remote
      {:url "/api"
       :request-middleware
       (comp (http/wrap-fulcro-request)
             (http/wrap-csrf-token js/window.fulcro_network_csrf_token))})}

    :remote-error?
    (fn [result]
      (let [{:keys [status-code]} result]
        (when-not (= status-code 200)
          (log/errorf "Error: %s" result)
          (js/console.log result))))}))
