(ns dinsro.model.core.tx-out
  (:refer-clojure :exclude [hash sequence time type])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.core.transactions :as m.c.transactions]
   [lambdaisland.glogc :as log]))

(>def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::n number?)
(defattr n ::n :int
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::value number?)
(defattr value ::value :int
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::address (s/or :string string? :nil nil?))
(defattr address ::address :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::asm (s/or :string string? :nil nil?))
(defattr asm ::asm :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::hex (s/or :string string? :nil nil?))
(defattr hex ::hex :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::type (s/or :string string? :nil nil?))
(defattr type ::type :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::transaction uuid?)
(defattr transaction ::transaction :ref
  {ao/identities #{::id}
   ao/target     ::m.c.transactions/id
   ao/schema     :production
   ::report/column-EQL {::transaction [::m.c.transactions/id ::m.c.transactions/tx-id]}})

(s/def ::params
  (s/keys :req [::n ::value ::transaction]
          :opt [::asm ::hex ::type
                ;; ::address
                ]))

(s/def ::item
  (s/keys :req [::id ::n ::value ::transaction]
          :opt [::asm ::hex ::type
                ;; ::address
                ]))

(>def ::unprepared-params
  (s/keys
   :req [:dinsro.client.converters.rpc-transaction-output/n
         :dinsro.client.converters.rpc-transaction-output/script-pub-key
         :dinsro.client.converters.rpc-transaction-output/value]))

(>defn prepare-params
  [params]
  [::unprepared-params => ::params]
  (log/info :prepare-params/starting {:params params})
  (let [{::keys [transaction]
         :dinsro.client.converters.rpc-transaction-output/keys
         [n script-pub-key]}           params
        currency-value                 (:dinsro.client.converters.rpc-transaction-output/value params)
        value                          (:dinsro.client.converters.currency-unit/value currency-value)
        {:dinsro.client.converters.rpc-script-pub-key/keys [asm hex script-type]} script-pub-key
        prepared-params                {::n           n
                                        ::value       value
                                        ::transaction transaction
                                        ::asm         asm
                                        ::hex         hex
                                        ::type        script-type
                                        ;; ::address     address
                                        }]
    (log/info :prepare-params/finished {:prepared-params prepared-params})
    prepared-params))

(s/def ::ident (s/keys :req [::id]))
(>defn ident [id] [::id => ::ident] {::id id})
(>defn idents [ids] [(s/coll-of ::id) => (s/coll-of ::ident)] (mapv ident ids))

(def attributes [id n value transaction asm hex type address])
