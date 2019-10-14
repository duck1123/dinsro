(ns dinsro.actions.status
  (:require [ring.util.http-response :refer [ok]]
            [taoensso.timbre :as timbre]))

(defn status-response
  [request]
  (ok {:userId 1}))
