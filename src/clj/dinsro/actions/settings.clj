(ns dinsro.actions.settings
  (:require [dinsro.model.settings :as m.settings]
            [dinsro.translations :refer [tr]]
            [ring.util.http-response :as http]
            [taoensso.timbre :as timbre]))

(defn settings-handler
  [_request]
  (http/ok (m.settings/get-settings)))
