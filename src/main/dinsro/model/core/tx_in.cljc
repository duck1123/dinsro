(ns dinsro.model.core.tx-in
  (:refer-clojure :exclude [hash sequence time])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.core.transactions :as m.c.transactions]
   [lambdaisland.glogc :as log]))

(s/def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::coinbase (s/or :string string? :nil nil?))
(defattr coinbase ::coinbase :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::txinwitness any?)
(defattr txinwitness ::txinwitness :list
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::scriptPubKey (s/or :string string? :nil nil?))
(defattr script-pub-key ::script-pub-key :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::tx-id (s/or :string string? :nil nil?))
(defattr tx-id ::tx-id :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::sequence number?)
(defattr sequence ::sequence :long
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::transaction uuid?)
(defattr transaction ::transaction :ref
  {ao/identities #{::id}
   ao/target     ::m.c.transactions/id
   ao/schema     :production
   ::report/column-EQL {::transaction [::m.c.transactions/id ::m.c.transactions/name]}})

(s/def ::txid (s/or :string string? :nil nil?))
(defattr txid ::txid :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::vout (s/or :int number? :nil nil?))
(defattr vout ::vout :int
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::params
  (s/keys :req [::sequence ::transaction]
          :opt [::vout ::script-pub-key ::txid ::coinbase]))

(s/def ::item
  (s/keys :req [::id ::sequence ::transaction]
          :opt [::vout ::script-pub-key ::txid ::coinbase]))

(>def ::unprepared-params
  (s/keys
   :req [:dinsro.client.converters.get-raw-transaction-vin/product-element-names
         :dinsro.client.converters.get-raw-transaction-vin/script-sig
         :dinsro.client.converters.get-raw-transaction-vin/sequence
         :dinsro.client.converters.get-raw-transaction-vin/tx-id
         :dinsro.client.converters.get-raw-transaction-vin/tx-in-witness
         :dinsro.client.converters.get-raw-transaction-vin/vout]))

(>defn prepare-params
  [params]
  [::unprepared-params => ::params]
  (let [{::keys [transaction]
         :dinsro.client.converters.get-raw-transaction-vin/keys
         [sequence script-sig vout tx-in-witness tx-id]} params
        coinbase                                   nil
        prepared-params                            {::sequence       sequence
                                                    ::transaction    transaction
                                                    ::vout           vout
                                                    ::script-pub-key script-sig
                                                    ::tx-id tx-id
                                                    ::tx-in-witness  tx-in-witness
                                                    ::coinbase       coinbase}]
    (log/info :prepare-params/finished {:prepared-params prepared-params})
    prepared-params))

(s/def ::ident (s/keys :req [::id]))
(>defn ident [id] [::id => ::ident] {::id id})
(>defn idents [ids] [(s/coll-of ::id) => (s/coll-of ::ident)] (mapv ident ids))

(def attributes [id sequence transaction vout script-pub-key
                 txinwitness
                 txid vout coinbase tx-id])
