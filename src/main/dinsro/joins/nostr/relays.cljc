(ns dinsro.joins.nostr.relays
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.nostr.relays :as m.n.relays]
   #?(:clj [dinsro.queries.nostr.relays :as q.n.relays])
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

;; [[../../actions/nostr/relays.clj][Actions]]
;; [[../../model/nostr/relays.cljc][Model]]
;; [[../../queries/nostr/relays.clj][Queries]]
;; [[../../ui/nostr/relays.cljs][UI]]

(defattr index ::index :ref
  {ao/target    ::m.n.relays/id
   ao/pc-output [{::index [::m.n.relays/id]}]
   ao/pc-resolve
   (fn [env _]
     (comment env)
     (let [ids #?(:clj (q.n.relays/index-ids) :cljs [])]
       (log/info :index/starting {:ids ids})
       {::index (m.n.relays/idents ids)}))})

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.n.relays/id
   ao/pc-output [{::admin-index [::m.n.relays/id]}]
   ao/pc-resolve
   (fn [_env _]
     (let [ids #?(:clj (q.n.relays/index-ids) :cljs [])]
       {::admin-index (m.n.relays/idents ids)}))})

(def attributes [admin-index index])
