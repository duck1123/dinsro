(ns dinsro.processors.nostr.filter-items
  (:require
   [dinsro.actions.nostr.filter-items :as a.n.filter-items]
   [dinsro.model.nostr.filter-items :as m.n.filter-items]
   [lambdaisland.glogc :as log]))

;; [[../../actions/nostr/filter_items.clj]]
;; [[../../model/nostr/filter_items.cljc]]

(def model-key ::m.n.filter-items/id)

(defn delete!
  [props]
  (log/info :delete!/starting {:props props})
  (let [id (model-key props)]
    (a.n.filter-items/delete! id)))
