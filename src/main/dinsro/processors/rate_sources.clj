(ns dinsro.processors.rate-sources
  (:require
   [dinsro.actions.rate-sources :as a.rate-sources]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.mutations :as mu]
   [dinsro.responses.rate-sources :as r.rate-sources]
   [lambdaisland.glogc :as log]))

;; [../model/rate_sources.cljc]
;; [../mutations/rate_sources.cljc]

(defn create!
  [props]
  (log/info :create/starting {:props props})
  (a.rate-sources/create! props)
  {::mu/status :ok})

(defn delete!
  [_env props]
  (log/info :create/starting {:props props})
  (let [rate-source-id (::m.rate-sources/id props)]
    (a.rate-sources/delete! rate-source-id)
    {::mu/status :ok
     ::mu/errors []
     ::r.rate-sources/deleted-records (m.rate-sources/idents [rate-source-id])}))
