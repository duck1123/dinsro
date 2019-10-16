(ns dinsro.actions.currencies
  (:require [dinsro.model.currencies :as m.currencies]
            [ring.util.http-response :as http]
            [taoensso.timbre :as timbre]))

(defn index
  [request]
  (let [items (m.currencies/index)]
    (http/ok {:items items})))
