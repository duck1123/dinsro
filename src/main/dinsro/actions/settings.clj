(ns dinsro.actions.settings
  (:require
   [dinsro.queries.settings :as q.settings]
   [dinsro.translations :refer [tr]]
   [ring.util.http-response :as http]
   [taoensso.timbre :as timbre]))

(defn settings-handler
  [_request]
  (http/ok (q.settings/get-settings)))
