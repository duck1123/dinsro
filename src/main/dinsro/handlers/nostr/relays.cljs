(ns dinsro.handlers.nostr.relays
  (:require
   [dinsro.model.nostr.relays :as m.n.relays]
   [lambdaisland.glogc :as log]))

(defn handle-connect
  [{:keys [state result] :as env}]
  (log/info :handle-connect/starting {:env env})
  (let [{:keys [body]} result
        data           (get body `toggle!)
        relay          (get data ::m.n.relays/item)]
    (comment state env)
    (log/info :handle-connect/started {:result result :body body :data data :relay relay})
    {}))
