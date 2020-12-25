(ns dinsro.resolvers.currencies
  (:require
   [com.wsscode.pathom.connect :as pc :refer [defresolver]]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.sample :as sample]
   [taoensso.timbre :as timbre]))

(defresolver currencies-resolver
  [_env _props]
  {::pc/output [{:all-currencies [::m.currencies/id]}]}
  {:all-currencies (map (fn [id] [::m.currencies/id id])
                        (keys sample/currency-map))})

(defresolver currency-resolver
  [_env {::m.currencies/keys [id]}]
  {::pc/input #{::m.currencies/id}
   ::pc/output [::m.currencies/name]}
  (get sample/currency-map id))

(defresolver currency-map-resolver
  [_env _props]
  {::pc/output [::m.currencies/map]}
  {::m.currencies/map sample/currency-map})

(def resolvers [currencies-resolver currency-resolver currency-map-resolver])
