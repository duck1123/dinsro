(ns dinsro.actions.status
  (:require
   [clojure.spec.alpha :as s]
   [ring.util.http-response :as http]
   [taoensso.timbre :as timbre]))

(defn status-handler
  [request]
  (let [{{:keys [identity]} :session} request]
    (http/ok {:identity identity})))

(s/fdef status-handler
  :args (s/cat :request (s/keys))
  :ret (s/keys))
