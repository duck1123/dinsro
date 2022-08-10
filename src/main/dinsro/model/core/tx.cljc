(ns dinsro.model.core.tx
  (:refer-clojure :exclude [hash sequence time])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.core.blocks :as m.c.blocks]
   [lambdaisland.glogc :as log]))

(def rename-map
  {:blockhash     ::block-hash
   :blocktime     ::block-time
   :confirmations ::confirmations
   :hash          ::hash
   :hex           ::hex
   :locktime      ::lock-time
   :size          ::size
   :time          ::time
   :txid          ::tx-id
   :vsize         ::vsize
   :version       ::version
   :vout          ::vout
   :vin           ::vin
   :weight        ::weight})

(s/def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::tx-id string?)
(defattr tx-id ::tx-id :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::block-hash string?)
(defattr block-hash ::block-hash :uuid
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::block-time number?)
(defattr block-time ::block-time :int
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::confirmations int?)
(defattr confirmations ::confirmations :int
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::hash string?)
(defattr hash ::hash :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::hex string?)
(defattr hex ::hex :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::lock-time number?)
(defattr lock-time ::lock-time :int
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::size number?)
(defattr size ::size :int
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::time number?)
(defattr time ::time :int
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::version number?)
(defattr version ::version :int
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::block uuid?)
(defattr block ::block :ref
  {ao/identities #{::id}
   ao/target     ::m.c.blocks/id
   ao/schema     :production
   ::report/column-EQL {::block [::m.c.blocks/id ::m.c.blocks/height ::m.c.blocks/hash]}})

(s/def ::fetched? boolean?)
(defattr fetched? ::fetched? :boolean
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::id-obj (s/keys :req [::id]))
(s/def ::params
  (s/keys :req [::tx-id
                ::block
                ::fetched?]
          :opt [::hash ::hex ::lock-time ::size ::time ::version]))
(s/def ::item
  (s/keys :req [::id
                ::tx-id
                ::block
                ::fetched?]
          :opt [::hash ::hex ::lock-time ::size ::time ::version]))

(>def ::unprepared-params
  (s/keys
   :req [::block ::fetched?]))

(>defn prepare-params
  [params]
  [::unprepared-params => ::params]
  (log/info :prepare-params/starting {:params params})
  (let [{::keys [block fetched?]
         :dinsro.client.converters.get-raw-transaction-result/keys
         [tx-id hash hex locktime size time version]} params
        prepared-params
        {::tx-id     tx-id
         ::block     block
         ::fetched?  fetched?
         ::hash      hash
         ::hex       (some->  hex :dinsro.client.converters.witness-transaction/bytes)
         ::lock-time locktime
         ::size      size
         ::time      time
         ::version   version}]
    (log/info :prepare-params/finished {:prepared-params prepared-params})
    prepared-params))

(defn idents
  [ids]
  (mapv (fn [id] {::id id}) ids))

(def attributes
  [id fetched? block
   hash hex lock-time tx-id
   size time version])
