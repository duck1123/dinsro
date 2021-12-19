(ns dinsro.model.core-block
  (:refer-clojure :exclude [hash time])
  (:require
   [clojure.set :as set]
   [clojure.spec.alpha :as s]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]

   [dinsro.model.core-nodes :as m.core-nodes]
   [taoensso.timbre :as log]))

(def rename-map
  {:bits          ::bits
   :chainwork     ::chainwork
   :confirmations ::confirmations
   :difficulty    ::difficlty
   :hash          ::hash
   :height        ::height
   :merkleRoot    ::merkle-root
   :nonce         ::nonce
   :previousHash  ::previous-hash
   :size          ::size
   :time          ::time
   :tx            ::tx
   :version       ::version})

(s/def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::bits string?)
(defattr bits ::bits :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::chainwork string?)
(defattr chainwork ::chainwork :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::confirmations int?)
(defattr confirmations ::confirmations :int
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::difficulty number?)
(defattr difficulty ::difficulty :int
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::hash string?)
(defattr hash ::hash :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::height number?)
(defattr height ::height :int
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::merkle-root string?)
(defattr merkle-root ::merkle-root :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::nonce number?)
(defattr nonce ::nonce :long
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

(s/def ::node uuid?)
(defattr node ::node :ref
  {ao/identities #{::id}
   ao/target     ::m.core-nodes/id
   ao/schema     :production
   ::report/column-EQL {::node [::m.core-nodes/id ::m.core-nodes/name]}})

(s/def ::status string?)
(defattr status ::status :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::fetched? boolean?)
(defattr fetched? ::fetched? :boolean
  {ao/identities #{::id}
   ao/schema     :production})

(defn prepare-params
  [block]
  (-> block
      (set/rename-keys rename-map)
      (dissoc ::confirmations)))

(s/def ::params
  (s/keys :req [::hash ::height ::node ::fetched?]
          :opt [::bits ::chainwork
                ;; ::confirmations
                ::difficlty ::merkle-root ::nonce
                ;; ::previous-hash
                ::size ::time
                ;; ::tx
                ::version]))
(s/def ::item
  (s/keys :req [::id ::hash ::height ::node ::fetched?]
          :opt [::bits ::chainwork
                ;; ::confirmations
                ::difficlty ::merkle-root ::nonce
                ;; ::previous-hash
                ::size ::time
                ;; ::tx
                ::version]))

(defn idents
  [ids]
  (mapv (fn [id] {::id id}) ids))

(def attributes
  [id bits chainwork
   ;; confirmations
   difficulty hash height
   merkle-root nonce size time version
   fetched? node])
