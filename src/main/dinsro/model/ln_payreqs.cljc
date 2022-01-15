(ns dinsro.model.ln-payreqs
  (:refer-clojure :exclude [name])
  (:require
   [clojure.set :as set]
   [clojure.spec.alpha :as s]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.ln-nodes :as m.ln-nodes]
   [taoensso.timbre :as log]
   [tick.alpha.api :as tick]))

(def rename-map
  {:description     ::description
   :features        ::features
   :cltvExpiry      ::cltv-expiry
   :expiry          ::expiry
   :paymentHash     ::payment-hash
   :paymentAddr     ::payment-address
   :numSatoshis     ::num-satoshis
   :fallbackAddr    ::fallback-address
   :numMsat         ::num-msats
   :descriptionHash ::description-hash
   :routeHints      ::route-hints
   :timestamp       ::timestamp
   :destination     ::destination})

(defn prepare-params
  [params]
  (let [timestamp (:timestamp params)]
    (-> (set/rename-keys params rename-map)
        (assoc ::timestamp (tick/instant (* timestamp 1000))))))

(s/def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::description string?)
(defattr description ::description :string
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

(s/def ::payment-hash string?)
(defattr payment-hash ::payment-hash :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::num-satoshis number?)
(defattr num-satoshis ::num-satoshis :long
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::fallback-address string?)
(defattr fallback-address ::fallback-address :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::num-msats number?)
(defattr num-msats ::num-msats :long
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::description-hash string?)
(defattr description-hash ::description-hash :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::timestamp inst?)
(defattr timestamp ::timestamp :date
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::destination string?)
(defattr destination ::destination :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::payment-request string?)
(defattr payment-request ::payment-request :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::node uuid?)
(defattr node ::node :ref
  {ao/identities       #{::id}
   ao/target           ::m.ln-nodes/id
   ao/schema           :production
   ::report/column-EQL {::node [::m.ln-nodes/id ::m.ln-nodes/name]}})

(s/def ::params
  (s/keys :req [::node]
          :opt [::payment-hash]))
(s/def ::item
  (s/keys :req [::id ::node]
          :opt [::payment-hash]))

(defn idents
  [ids]
  (mapv (fn [id] {::id id}) ids))

(def attributes
  [id
   description
   cltv-expiry
   expiry
   payment-hash
   num-satoshis
   fallback-address
   num-msats
   description-hash
   timestamp
   destination
   payment-request
   node])
