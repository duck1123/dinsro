(ns dinsro.actions.status
  (:require [ring.util.http-response :as http]
            [taoensso.timbre :as timbre]))

(defn status-handler
  [request]
  (let [{{:keys [identity]} :session} request]
    (http/ok {:identity identity})))
