(ns dinsro.processors.categories
  (:require
   [dinsro.actions.categories :as a.categories]
   [dinsro.model.categories :as m.categories]
   [dinsro.mutations :as mu]
   [dinsro.responses.categories :as r.categories]
   [lambdaisland.glogc :as log]))

;; [../actions/categories.clj]
;; [../joins/categories.cljc]
;; [../model/categories.cljc]
;; [../responses/categories.cljc]
;; [../ui/categories.cljs]

(defn delete!
  [_env props]
  (log/info :delete!/starting {:props props})
  (let [{::m.categories/keys [id]} props]
    (a.categories/delete! id)
    {::mu/status :ok ::r.categories/deleted-records (m.categories/idents [id])}))
