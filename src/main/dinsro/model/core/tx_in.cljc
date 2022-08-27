(ns dinsro.model.core.tx-in
  (:refer-clojure :exclude [hash sequence time])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.core.tx :as m.c.tx]
   [lambdaisland.glogc :as log]))

(def rename-map
  {:coinbase    ::coinbase
   :txinwitness ::txinwitness
   :sequence    ::sequence})

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
   ao/target     ::m.c.tx/id
   ao/schema     :production})

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

(def attributes [id sequence transaction vout script-pub-key txid vout coinbase tx-id])
