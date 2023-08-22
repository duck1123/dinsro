(ns dinsro.processors.core.addresses
  (:require
   [dinsro.actions.core.addresses :as a.c.addresses]
   [dinsro.model.core.addresses :as m.c.addresses]
   [dinsro.mutations :as mu]
   [dinsro.responses.core.addresses :as r.c.addresses]))

;; [[../../actions/core/addresses.clj]]
;; [[../../mutations/core/addresses.cljc]]

(def model-key ::m.c.addresses/id)

(defn delete!
  [_env props]
  (let [id (model-key props)]
    (a.c.addresses/delete! id)
    {::mu/status                     :ok
     ::r.c.addresses/deleted-records (m.c.addresses/idents [id])}))
