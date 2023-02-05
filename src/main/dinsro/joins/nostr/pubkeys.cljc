(ns dinsro.joins.nostr.pubkeys
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   #?(:clj [dinsro.queries.nostr.pubkeys :as q.n.pubkeys])
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

;; [[../../actions/nostr/pubkeys.clj][Pubkey Actions]]
;; [[../../model/nostr/pubkeys.cljc][Pubkeys Model]]
;; [[../../queries/nostr/pubkeys.clj][Pubkey Queries]]
;; [[../../ui/nostr/pubkeys.cljs][Pubkeys UI]]

(defattr index ::index :ref
  {ao/target    ::m.n.pubkeys/id
   ao/pc-output [{::index [::m.n.pubkeys/id]}]
   ao/pc-resolve
   (fn [_env _]
     (log/info :index/starting {})
     (let [ids #?(:clj (q.n.pubkeys/index-ids) :cljs [])]
       {::index (m.n.pubkeys/idents ids)}))})

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.n.pubkeys/id
   ao/pc-output [{::admin-index [::m.n.pubkeys/id]}]
   ao/pc-resolve
   (fn [_env _]
     (log/info :admin-index/starting {})
     (let [ids #?(:clj (q.n.pubkeys/index-ids) :cljs [])]
       {::admin-index (m.n.pubkeys/idents ids)}))})

(defattr contacts ::contacts :ref
  {ao/target    ::m.n.pubkeys/id
   ao/pc-output [{::contacts [::m.n.pubkeys/id]}]
   ao/pc-resolve
   (fn [_env params]
     (if-let [pubkey-id (::m.n.pubkeys/id params)]
       (let [ids       #?(:clj  (q.n.pubkeys/find-contacts pubkey-id)
                          :cljs (do (comment pubkey-id) []))]
         {::contacts (m.n.pubkeys/idents ids)})
       #?(:clj (throw (RuntimeException. "No pubkey supplied"))
          :cljs (throw (js/Error. "No pubkey supplied")))))})

(defattr contact-count ::contact-count :int
  {ao/identities #{::m.n.pubkeys/id}
   ao/pc-input   #{::m.n.pubkeys/id}
   ao/pc-resolve
   (fn [_env params]
     (log/info :contact-count/starting {:params params})
     (let [pubkey-id (::m.n.pubkeys/id params)
           pubkeys   #?(:clj (q.n.pubkeys/find-contacts pubkey-id)
                        :cljs (do (comment pubkey-id) []))]
       {::contact-count (count pubkeys)}))})

(def attributes [admin-index index contact-count contacts])
