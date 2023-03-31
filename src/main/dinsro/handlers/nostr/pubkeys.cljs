(ns dinsro.handlers.nostr.pubkeys
  (:require
   [com.fulcrologic.fulcro.algorithms.merge :as merge]
   [dinsro.model.core.nodes :as m.c.nodes]
   [lambdaisland.glogc :as log]))

(defn handle-fetch
  [{:keys [state] :as env}]
  (let [body                                        (get-in env [:result :body])
        response                                    (get body `fetch!)
        {:com.fulcrologic.rad.pathom/keys [errors]} response]
    (if errors
      (do
        (log/error :handle-fetch/errored {:errors errors})
        {})
      (let [status (:dinsro.mutations/status response)]
        (if (= status :error)
          (let [errors (:dinsro.mutations/errors response)]
            (log/info :handle-fetch/errored {:response response :errors errors})
            {})
          (do
            (log/info :handle-fetch/completed {:response response})
            (let [{::m.c.nodes/keys [item]} response
                  {::m.c.nodes/keys [id]}   item]
              (swap! state #(merge/merge-ident % [::m.c.nodes/id id] item))
              {})))))))

(defn handle-fetch-contacts
  [{:keys [state] :as env}]
  (let [body                                        (get-in env [:result :body])
        response                                    (get body `fetch!)
        {:com.fulcrologic.rad.pathom/keys [errors]} response]
    (if errors
      (do
        (log/error :handle-fetch/errored {:errors errors})
        {})
      (let [status (:dinsro.mutations/status response)]
        (if (= status :error)
          (let [errors (:dinsro.mutations/errors response)]
            (log/info :handle-fetch/errored {:response response :errors errors})
            {})
          (do
            (log/info :handle-fetch/completed {:response response})
            (let [{::m.c.nodes/keys [item]} response
                  {::m.c.nodes/keys [id]}   item]
              (swap! state #(merge/merge-ident % [::m.c.nodes/id id] item))
              {})))))))

(defn handle-fetch-events
  [{:keys [state] :as env}]
  (let [body                                        (get-in env [:result :body])
        response                                    (get body `fetch!)
        {:com.fulcrologic.rad.pathom/keys [errors]} response]
    (if errors
      (do
        (log/error :handle-fetch-events/errored {:errors errors})
        {})
      (let [status (:dinsro.mutations/status response)]
        (if (= status :error)
          (let [errors (:dinsro.mutations/errors response)]
            (log/info :handle-fetch-events/errored {:response response :errors errors})
            {})
          (do
            (log/info :handle-fetch-events/completed {:response response})
            (let [{::m.c.nodes/keys [item]} response
                  {::m.c.nodes/keys [id]}   item]
              (swap! state #(merge/merge-ident % [::m.c.nodes/id id] item))
              {})))))))
