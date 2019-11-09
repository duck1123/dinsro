(ns dinsro.actions.rates
  (:require [dinsro.model.rates :as m.rates]
            [ring.util.http-response :as http]
            [taoensso.timbre :as timbre]))

(defn index-handler
  [request]
  (let [items (m.rates/index-records)]
    (http/ok {:model :rates :items items})))
