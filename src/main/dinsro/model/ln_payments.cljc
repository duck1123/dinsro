(ns dinsro.model.ln-payments
  (:require
   [clojure.set :as set]
   [clojure.spec.alpha :as s]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.ln-nodes :as m.ln-nodes]
   [taoensso.timbre :as log]))

(def rename-map
  {:paymentPreimage ::payment-preimage
   :paymentHash     ::payment-hash
   :paymentRequest  ::payment-request
   :status          ::status
   :fee             ::fee
   :value           ::value
   :paymentIndex    ::payment-index
   :failureReason   ::failure-reason
   :creationDate    ::creation-date})

(defn prepare-params
  [params]
  (set/rename-keys params rename-map))

(s/def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::payment-preimage string?)
(defattr payment-preimage ::payment-preimage :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::payment-hash string?)
(defattr payment-hash ::payment-hash :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::payment-request string?)
(defattr payment-request ::payment-request :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::status string?)
(defattr status ::status :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::fee number?)
(defattr fee ::fee :long
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::value number?)
(defattr value ::value :long
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::payment-index number?)
(defattr payment-index ::payment-index :long
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::failure-reason string?)
(defattr failure-reason ::failure-reason :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::creation-date number?)
(defattr creation-date ::creation-date :long
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::node uuid?)
(defattr node ::node :ref
  {ao/identities       #{::id}
   ao/target           ::m.ln-nodes/id
   ao/schema           :production
   ::report/column-EQL {::node [::m.ln-nodes/id ::m.ln-nodes/name]}})

(s/def ::params
  (s/keys :req [::payment-preimage ::payment-hash ::payment-request ::status ::fee ::value
                ::payment-index ::failure-reason ::creation-date ::node]))
(s/def ::item
  (s/keys :req [::id ::payment-preimage ::payment-hash ::payment-request ::status ::fee ::value
                ::payment-index ::failure-reason ::creation-date ::node]))

(defn idents
  [ids]
  (mapv (fn [id] {::id id}) ids))

(def attributes
  [id payment-preimage payment-hash payment-request status fee value payment-index failure-reason
   creation-date node])
