(ns dinsro.processors.nostr.filters
  (:require
   [dinsro.actions.nostr.filters :as a.n.filters]
   [dinsro.model.nostr.filters :as m.n.filters]
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.mutations :as mu]
   [lambdaisland.glogc :as log]))

;; [[../../actions/nostr/filters.clj]]
;; [[../../joins/nostr/filters.cljc]]

(def model-key ::m.n.filters/id)

(defn add-filters!
  [props]
  (log/info :do-add-filters!/starting {:props props})
  (if-let [request-id (::m.n.requests/id props)]
    (do
      (a.n.filters/add-filter! request-id)
      {::mu/status :ok})
    {::mu/status :fail
     ::mu/errors ["No request id"]}))

(defn delete!
  [props]
  (log/info :do-delete!/starting {:props props})
  (let [id (model-key props)]
    (a.n.filters/delete! id))
  {::mu/status :ok})
