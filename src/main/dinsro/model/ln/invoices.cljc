(ns dinsro.model.ln.invoices
  (:require
   [clojure.set :as set]
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [tick.alpha.api :as tick]))

(def rename-map
  {:amtPaid         ::ammount-paid
   :addIndex        ::add-index
   :cltvExpiry      ::cltv-expiry
   :private         ::private?
   :isKeysend       ::keysend?
   :value           ::value
   :rHash           ::r-hash
   :rPreimage       ::r-preimage
   :paymentAddr     ::payment-address
   :paymentRequest  ::payment-request
   :state           ::state
   :settled         ::settled?
   :fallbackAddr    ::fallback-address
   :settleDate      ::settle-date
   :settleIndex     ::settle-index
   :descriptionHash ::description-hash
   :isAmp           ::amp?
   :creationDate    ::creation-date
   :expiry          ::expiry
   :memo            ::memo})

(defn prepare-params
  [params]
  (let [creation-date (:creationDate params)]
    (-> (set/rename-keys params rename-map)
        (dissoc :amtPaidMsat)
        (dissoc :amtPaidSat)
        (dissoc :valueMsat)
        (dissoc :routeHints)
        (dissoc :htlcs)
        (assoc ::creation-date (tick/instant (* creation-date 1000))))))

(s/def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::ammount-paid number?)
(defattr ammount-paid ::ammount-paid :long
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::add-index number?)
(defattr add-index ::add-index :long
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::cltv-expiry number?)
(defattr cltv-expiry ::cltv-expiry :long
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::expiry number?)
(defattr expiry ::expiry :long
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::private? boolean?)
(defattr private? ::private? :boolean
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::keysend? boolean?)
(defattr keysend? ::keysend? :boolean
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::value number?)
(defattr value ::value :long
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::r-hash string?)
(defattr r-hash ::r-hash :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::r-preimage string?)
(defattr r-preimage ::r-preimage :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::payment-request string?)
(defattr payment-request ::payment-request :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::payment-address string?)
(defattr payment-address ::payment-address :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::state string?)
(defattr state ::state :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::settled? boolean?)
(defattr settled? ::settled? :boolean
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::fallback-address string?)
(defattr fallback-address ::fallback-address :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::settle-date number?)
(defattr settle-date ::settle-date :long
  {ao/identities #{::id}
   ao/schema     :production})

;; (s/def ::ammount-paid number?)
;; (defattr ammount-paid ::ammount-paid :long
;;   {ao/identities #{::id}
;;    ao/schema     :production})

(s/def ::settle-index number?)
(defattr settle-index ::settle-index :long
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::description-hash string?)
(defattr description-hash ::description-hash :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::amp? boolean?)
(defattr amp? ::amp? :boolean
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::creation-date inst?)
(defattr creation-date ::creation-date :date
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::memo string?)
(defattr memo ::memo :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::node uuid?)
(defattr node ::node :ref
  {ao/identities       #{::id}
   ao/target           ::m.ln.nodes/id
   ao/schema           :production
   ::report/column-EQL {::node [::m.ln.nodes/id ::m.ln.nodes/name]}})

(s/def ::params
  (s/keys :req [::ammount-paid ::add-index ::cltv-expiry ::expiry ::private?
                ::keysend? ::value ::r-hash ::r-preimage ::payment-request ::state
                ::settled? ::fallback-address ::settle-date ::settle-index ::description-hash
                ::amp? ::creation-date ::memo ::node]))
(s/def ::item
  (s/keys :req [::id ::ammount-paid ::add-index ::cltv-expiry ::expiry ::private?
                ::keysend? ::value ::r-hash ::r-preimage ::payment-request ::state
                ::settled? ::fallback-address ::settle-date ::settle-index ::description-hash
                ::amp? ::creation-date ::memo ::node]))

(>def ::ident (s/keys :req [::id]))
(>defn ident [id] [::id => ::ident] {::id id})
(>defn idents [ids] [(s/coll-of ::id) => (s/coll-of ::ident)] (mapv ident ids))

(def attributes
  [id ammount-paid add-index cltv-expiry expiry private? keysend? value r-hash r-preimage
   payment-request state settled? fallback-address settle-date settle-index description-hash
   amp? creation-date
   memo node])
