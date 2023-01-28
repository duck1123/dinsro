(ns dinsro.joins.nostr.pubkeys
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   #?(:clj [dinsro.queries.nostr.pubkeys :as q.n.pubkeys])
   [dinsro.specs]))

;; [[../../actions/nostr/pubkeys.clj][Pubkey Actions]]
;; [[../../model/nostr/pubkeys.cljc][Pubkeys Model]]
;; [[../../queries/nostr/pubkeys.clj][Pubkey Queries]]
;; [[../../ui/nostr/pubkeys.cljs][Pubkeys UI]]

(defattr index ::index :ref
  {ao/target     ::m.n.pubkeys/id
   ao/pc-output  [{::m.n.pubkeys/index [::m.n.pubkeys/id]}]
   ao/pc-resolve (fn [_env _]
                   (let [ids #?(:clj (q.n.pubkeys/index-ids) :cljs [])]
                     {::index (m.n.pubkeys/idents ids)}))})

(defattr admin-index ::admin-index :ref
  {ao/target     ::m.n.pubkeys/id
   ao/pc-output  [{::admin-index [::m.n.pubkeys/id]}]
   ao/pc-resolve (fn [_env _]
                   (let [ids #?(:clj (q.n.pubkeys/index-ids) :cljs [])]
                     {::admin-index (m.n.pubkeys/idents ids)}))})

(def attributes [admin-index index])
