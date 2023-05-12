(ns dinsro.processors.categories
  (:require
   [dinsro.actions.categories :as a.categories]
   [dinsro.model.categories :as m.categories]
   [dinsro.mutations :as mu]
   [lambdaisland.glogc :as log]))

;; [../actions/categories.clj]
;; [../model/categories.cljc]

(defn delete!
  [_env props]
  (log/info :delete!/starting {:props props})
  (let [{::m.categories/keys [id]} props]
    (a.categories/delete! id)
    {::mu/status :ok}))
