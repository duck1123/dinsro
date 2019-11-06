(ns dinsro.actions.currencies
  (:require [dinsro.model.currencies :as m.currencies]
            [ring.util.http-response :as http]
            [taoensso.timbre :as timbre]))

(defn index-handler
  [request]
  (let [items (m.currencies/index)]
    (http/ok {:items items})))

(defn create-handler
  [{:keys [params] :as request}]
  (let [item (m.currencies/create-record params)]
    (http/ok {:item item})))
