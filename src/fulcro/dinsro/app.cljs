(ns dinsro.app
  (:require
   [com.fulcrologic.fulcro.application :as app]
   [com.fulcrologic.fulcro.networking.http-remote :as http]
   [taoensso.timbre :as timbre]))

(defonce app
  (app/fulcro-app
   {:remotes
    {:remote
     (http/fulcro-http-remote
      {:url "/pathom"
       :request-middleware
       (comp (http/wrap-fulcro-request)
             (http/wrap-csrf-token js/window.csrfToken))})}
    :remote-error? (fn [result]
                     (let [{:keys [status-code]} result]
                       (when-not (= status-code 200)
                         (timbre/errorf "Error: %s" result)
                         (js/console.log result))))}))
