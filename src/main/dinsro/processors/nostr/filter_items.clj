(ns dinsro.processors.nostr.filter-items
  (:require
   [dinsro.actions.nostr.filter-items :as a.n.filter-items]
   [dinsro.model.nostr.filter-items :as m.n.filter-items]
   [dinsro.mutations :as mu]
   [dinsro.responses.nostr.filter-items :as r.n.filter-items]
   [lambdaisland.glogc :as log]))

;; [[../../actions/nostr/filter_items.clj]]
;; [[../../model/nostr/filter_items.cljc]]

(def model-key ::m.n.filter-items/id)

(defn delete!
  [_env props]
  (log/info :delete!/starting {:props props})
  (let [id (model-key props)]
    (a.n.filter-items/delete! id)
    {::mu/status                   :ok
     ::r.n.filter-items/deleted-records (m.n.filter-items/idents [id])}))
