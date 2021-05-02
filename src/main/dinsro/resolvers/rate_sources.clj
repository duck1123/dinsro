(ns dinsro.resolvers.rate-sources
  (:require
   [com.wsscode.pathom.connect :as pc :refer [defresolver]]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.queries.rate-sources :as q.rate-sources]
   [taoensso.timbre :as timbre]))

(defn resolve-rate-source
  [id]
  (let [record      (q.rate-sources/read-record id)
        id          (:db/id record)
        currency-id (get-in record [::m.rate-sources/currency ::m.currencies/id])]
    (-> record
        (assoc ::m.rate-sources/id id)
        (assoc ::m.rate-sources/currency [[::m.currencies/id currency-id]]))))

(defn resolve-rate-sources
  []
  (let [ids (q.rate-sources/index-ids)
        idents (map m.rate-sources/ident ids)]
    {:all-rate-sources idents}))

(defresolver rate-source-resolver
  [_env {::m.rate-sources/keys [id]}]
  {::pc/input  #{::m.rate-sources/id}
   ::pc/output [::m.rate-sources/name
                {::m.rate-sources/currency [::m.currencies/id]}
                ::m.rate-sources/url]}
  (resolve-rate-source id))

(defresolver rate-sources-resolver
  [_env _props]
  {::pc/output [{:all-rate-sources [::m.rate-sources/id]}]}
  (resolve-rate-sources))

(def resolvers [rate-source-resolver rate-sources-resolver])
