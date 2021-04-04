(ns dinsro.resolvers.rates
  (:require
   [com.wsscode.pathom.connect :as pc :refer [defresolver]]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rates :as m.rates]
   [dinsro.sample :as sample]
   [taoensso.timbre :as timbre]))

(defresolver rate-resolver
  [_env {::m.rates/keys [id]}]
  {::pc/input  #{::m.rates/id}
   ::pc/output [{::m.rates/currency [::m.currencies/id]}
                ::m.rates/date
                ::m.rates/rate]}
  (get sample/rate-map id))

(defresolver rates-resolver
  [_env _props]
  {::pc/output [{:all-rates [::m.rates/id]}]}
  {:all-rates (map (fn [id] [::m.rates/id id]) (keys sample/rate-map))})

(defresolver rate-map-resolver
  [_env _props]
  {::pc/output [::m.rates/map]}
  {::m.rates/map sample/rate-map})

(def resolvers [rate-resolver rates-resolver rate-map-resolver])
