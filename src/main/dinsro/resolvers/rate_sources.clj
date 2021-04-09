(ns dinsro.resolvers.rate-sources
  (:require
   [com.wsscode.pathom.connect :as pc :refer [defmutation defresolver]]
   [dinsro.actions.rate-sources :as a.rate-sources]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.queries.rate-sources :as q.rate-sources]
   [taoensso.timbre :as timbre]))

(defmutation create!
  [_env params]
  {::pc/params #{:name :url :currency-id}
   ::pc/output [:status
                {:item [::m.rate-sources/id]}]}
  (if-let [record (a.rate-sources/create! params)]
    {:status :success
     :item   [{::m.rate-sources/id (:db/id record)}]}
    {:status :failure}))

(defmutation delete!
  [_request {::m.rate-sources/keys [id]}]
  {::pc/params #{::m.rate-sources/id}
   ::pc/output [:status]}
  (q.rate-sources/delete-record id)
  {:status :success})

(defresolver rate-source-resolver
  [_env {::m.rate-sources/keys [id]}]
  {::pc/input  #{::m.rate-sources/id}
   ::pc/output [::m.rate-sources/name
                {::m.rate-sources/currency [::m.currencies/id]}
                ::m.rate-sources/url]}
  (let [record      (q.rate-sources/read-record id)
        id          (:db/id record)
        currency-id (get-in record [::m.rate-sources/currency :db/id])]
    (-> record
        (assoc ::m.rate-sources/id id)
        (assoc ::m.rate-sources/currency [[::m.currencies/id currency-id]]))))

(defresolver rate-sources-resolver
  [_env _props]
  {::pc/output [{:all-rate-sources [::m.rate-sources/id]}]}
  {:all-rate-sources (map (fn [id] [::m.rate-sources/id id]) (q.rate-sources/index-ids))})

(def resolvers [rate-source-resolver rate-sources-resolver])
