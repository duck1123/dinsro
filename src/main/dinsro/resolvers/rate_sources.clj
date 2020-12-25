(ns dinsro.resolvers.rate-sources
  (:require
   [com.wsscode.pathom.connect :as pc :refer [defresolver]]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.sample :as sample]
   [taoensso.timbre :as timbre]))

(defresolver rate-source-resolver
  [_env {::m.rate-sources/keys [id]}]
  {::pc/input #{::m.rate-sources/id}
   ::pc/output [::m.rate-sources/name
                {::m.rate-sources/currency [::m.currencies/id]}
                ::m.rate-sources/url]}
  (get sample/rate-source-map id))

(defresolver rate-source-map-resolver
  [_env _props]
  {::pc/output [::m.rate-sources/map]}
  {::m.rate-sources/map sample/rate-source-map})

(defresolver rate-sources-resolver
  [_env _props]
  {::pc/output [{:all-rate-sources [::m.rate-sources/id]}]}
  {:all-rate-sources (map (fn [id] [::m.rate-sources/id id]) (keys sample/rate-source-map))})

(def resolvers [rate-source-resolver rate-source-map-resolver rate-sources-resolver])
