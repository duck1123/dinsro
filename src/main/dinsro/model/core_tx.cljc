(ns dinsro.model.core-tx
  (:refer-clojure :exclude [hash sequence time])
  (:require
   [clojure.set :as set]
   [clojure.spec.alpha :as s]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.core-block :as m.core-block]
   [taoensso.timbre :as log]))

(def rename-map
  {:blockHash     ::block-hash
   :blockTime     ::block-time
   :confirmations ::confirmations
   :hash          ::hash
   :hex           ::hex
   :lockTime      ::lock-time
   :size          ::size
   :time          ::time
   :txId          ::tx-id
   :version       ::version})

(defn prepare-params
  [params]
  (-> params
      (set/rename-keys rename-map)
      (dissoc :vOut)
      (dissoc :vIn)))

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

(s/def ::block-time inst?)
(defattr block-time ::block-time :date
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

(s/def ::time inst?)
(defattr time ::time :date
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::version number?)
(defattr version ::version :int
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::block uuid?)
(defattr block ::block :ref
  {ao/identities #{::id}
   ao/target     ::m.core-block/id
   ao/schema     :production})

(s/def ::fetched? boolean?)
(defattr fetched? ::fetched? :boolean
  {ao/identities #{::id}
   ao/schema     :production})

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

(defn idents
  [ids]
  (mapv (fn [id] {::id id}) ids))

(def attributes
  [id fetched? block
   hash hex lock-time tx-id
   size time version])
