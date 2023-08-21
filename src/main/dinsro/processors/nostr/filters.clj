(ns dinsro.processors.nostr.filters
  (:require
   [dinsro.actions.nostr.filters :as a.n.filters]
   [dinsro.model.nostr.filters :as m.n.filters]
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.mutations :as mu]
   [dinsro.responses.nostr.filters :as r.n.filters]
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
  [_env props]
  (log/info :delete!/starting {:props props})
  (let [id (model-key props)]
    (a.n.filters/delete! id)
    {::mu/status                   :ok
     ::r.n.filters/deleted-records (m.n.filters/idents [id])}))
