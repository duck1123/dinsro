(ns dinsro.resolvers.rates
  (:require
   [com.wsscode.pathom.connect :as pc :refer [defresolver]]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rates :as m.rates]
   [dinsro.queries.rates :as q.rates]
   [taoensso.timbre :as timbre]))

(defresolver rate-resolver
  [_env {::m.rates/keys [id]}]
  {::pc/input  #{::m.rates/id}
   ::pc/output [{::m.rates/currency [::m.currencies/id]}
                ::m.rates/date
                ::m.rates/rate]}
  (let [record (q.rates/read-record id)]
    (assoc record ::m.rates/id id)))

(defresolver rates-resolver
  [_env _props]
  {::pc/output [{:all-rates [::m.rates/id]}]}
  {:all-rates (map (fn [id] [::m.rates/id id]) (q.rates/index-ids))})

(def resolvers [rate-resolver rates-resolver])
