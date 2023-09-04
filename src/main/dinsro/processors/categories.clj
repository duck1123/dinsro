(ns dinsro.processors.categories
  (:require
   [dinsro.actions.categories :as a.categories]
   [dinsro.model.categories :as m.categories]
   [dinsro.mutations :as mu]
   [dinsro.options.categories :as o.categories]
   [dinsro.responses.categories :as r.categories]
   [lambdaisland.glogc :as log]))

;; [[../actions/categories.clj]]
;; [[../joins/categories.cljc]]
;; [[../model/categories.cljc]]
;; [[../responses/categories.cljc]]
;; [[../ui/categories.cljs]]

(def model-key o.categories/id)

(defn create!
  [env props]
  (log/info :create!/starting {:props props})
  (let [actor-id      (get-in env [:query-params :actor/id])
        record-params (merge props {o.categories/user actor-id})
        id            (a.categories/create! record-params)]
    {mu/status                      :ok
     ::r.categories/deleted-records (m.categories/idents [id])}))

(defn delete!
  [_env props]
  (log/info :delete!/starting {:props props})
  (let [id (model-key props)]
    (a.categories/delete! id)
    {mu/status                      :ok
     ::r.categories/deleted-records (m.categories/idents [id])}))
