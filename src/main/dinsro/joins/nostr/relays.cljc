(ns dinsro.joins.nostr.relays
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.nostr.contact-relays :as m.n.contact-relays]
   [dinsro.model.nostr.relays :as m.n.relays]
   #?(:clj [dinsro.queries.nostr.contact-relays :as q.n.contact-relays])
   #?(:clj [dinsro.queries.nostr.relays :as q.n.relays])
   #?(:clj [dinsro.queries.nostr.subscriptions :as q.n.subscriptions])
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

;; [[../../actions/nostr/relays.clj][Actions]]
;; [[../../model/nostr/relays.cljc][Model]]
;; [[../../queries/nostr/relays.clj][Queries]]
;; [[../../ui/nostr/relays.cljs][UI]]

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.n.relays/id
   ao/pc-output [{::admin-index [::m.n.relays/id]}]
   ao/pc-resolve
   (fn [_env _]
     (let [ids #?(:clj (q.n.relays/index-ids) :cljs [])]
       {::admin-index (m.n.relays/idents ids)}))})

(defattr contact-count ::contact-count :int
  {ao/identities #{::m.n.contact-relays/id}
   ao/pc-input   #{::m.n.contact-relays/id}
   ao/pc-resolve
   (fn [_env params]
     (log/info :contact-count/starting {:params params})
     (let [relay-id (::m.n.relays/id params)
           ids      #?(:clj  (q.n.contact-relays/find-by-relay relay-id)
                       :cljs (do (comment relay-id) []))]
       {::contact-count (count ids)}))})

(defattr contacts ::contacts :int
  {ao/identities #{::m.n.contact-relays/id}
   ao/pc-input   #{::m.n.contact-relays/id}
   ao/pc-output  [{::contacts [::m.n.contact-relays/id]}]
   ao/pc-resolve
   (fn [_env params]
     (log/info :contacts/starting {:params params})
     (let [relay-id (::m.n.relays/id params)
           ids      #?(:clj  (q.n.contact-relays/find-by-relay relay-id)
                       :cljs (do (comment relay-id) []))
           idents   (m.n.contact-relays/idents ids)]
       {::contacts idents}))})

(defattr index ::index :ref
  {ao/target    ::m.n.relays/id
   ao/pc-output [{::index [::m.n.relays/id]}]
   ao/pc-resolve
   (fn [env _]
     (comment env)
     (let [ids #?(:clj (q.n.relays/index-ids) :cljs [])]
       (log/info :index/starting {:ids ids})
       {::index (m.n.relays/idents ids)}))})

(defattr subscription-count ::subscription-count :int
  {ao/identities #{::m.n.relays/id}
   ao/pc-input   #{::m.n.relays/id}
   ao/pc-resolve
   (fn [_env params]
     (log/info :subscription-count/starting {:params params})
     (let [relay-id (::m.n.relays/id params)
           ids  #?(:clj  (q.n.subscriptions/find-by-relay relay-id)
                   :cljs (do (comment relay-id) []))]
       {::subscription-count (count ids)}))})

(def attributes [admin-index contact-count contacts index subscription-count])
