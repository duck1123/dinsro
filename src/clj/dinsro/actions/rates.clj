(ns dinsro.actions.rates
  (:require [dinsro.model.rates :as m.rates]))

(defn index-handler
  [request]
  (let [items (m.rates/fetch-index)]
      (http/ok {:items items})))
