(ns dinsro.resolvers.rate-sources
  (:require
   [com.wsscode.pathom.connect :as pc :refer [defresolver]]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.queries.rate-sources :as q.rate-sources]
   [taoensso.timbre :as timbre]))

(defresolver rate-source-resolver
  [_env {::m.rate-sources/keys [id]}]
  {::pc/input #{::m.rate-sources/id}
   ::pc/output [::m.rate-sources/name
                {::m.rate-sources/currency [::m.currencies/id]}
                ::m.rate-sources/url]}
  (let [record (q.rate-sources/read-record id)
        id (:db/id record)]
    (assoc record ::m.rate-sources/id id)))

(defresolver rate-sources-resolver
  [_env _props]
  {::pc/output [{:all-rate-sources [::m.rate-sources/id]}]}
  {:all-rate-sources (map (fn [id] [::m.rate-sources/id id]) (q.rate-sources/index-ids))})

(def resolvers [rate-source-resolver rate-sources-resolver])
