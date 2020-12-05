(ns dinsro.actions.status
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [ring.util.http-response :as http]
   [taoensso.timbre :as timbre]))

(>defn status-handler
  [request]
  [(s/keys) => (s/keys)]
  (let [{{:keys [user]} :identity} request]
    (http/ok {:identity user})))
