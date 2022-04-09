(ns dinsro.model.core.blocks
  (:refer-clojure :exclude [hash time])
  (:require
   [clojure.set :as set]
   [clojure.spec.alpha :as s]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.core.nodes :as m.c.nodes]
   [tick.alpha.api :as tick]))

(def rename-map
  {:bits              ::bits
   :chainwork         ::chainwork
   :confirmations     ::confirmations
   :difficulty        ::difficlty
   :hash              ::hash
   :height            ::height
   :merkleroot        ::merkle-root
   :nonce             ::nonce
   :previousblockhash ::previous-block-hash
   :size              ::size
   :time              ::time
   :tx                ::tx
   :mediantime        ::median-time
   :strippedsize      ::stripped-size
   :weight            ::weight
   :version           ::version
   :versionHex        ::version-hex
   :nTx               ::transaction-count})

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

(s/def ::median-time int?)
(defattr median-time ::median-time :int
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::weight int?)
(defattr weight ::weight :int
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

(s/def ::version-hex string?)
(defattr version-hex ::version-hex :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::transaction-count number?)
(defattr transaction-count ::transaction-count :int
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::stripped-size number?)
(defattr stripped-size ::stripped-size :int
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::next-block (s/or :id uuid?
                          :nil nil?))
(defattr next-block ::next-block :ref
  {ao/identities       #{::id}
   ao/target           ::id
   ao/schema           :production
   ::report/column-EQL {::next-block [::id ::hash ::height]}})

(s/def ::previous-block (s/or :id uuid? :nil nil?))
(defattr previous-block ::previous-block :ref
  {ao/identities       #{::id}
   ao/target           ::id
   ao/schema           :production
   ::report/column-EQL {::previous-block [::id ::hash ::height]}})

(s/def ::node uuid?)
(defattr node ::node :ref
  {ao/identities       #{::id}
   ao/target           ::m.c.nodes/id
   ao/schema           :production
   ::report/column-EQL {::node [::m.c.nodes/id ::m.c.nodes/name]}})

(s/def ::fetched? boolean?)
(defattr fetched? ::fetched? :boolean
  {ao/identities #{::id}
   ao/schema     :production})

(defn prepare-params
  [block]
  (let [{:keys [time fetched?]} block]
    (-> block
        (set/rename-keys rename-map)
        (assoc ::time (tick/instant (* time 1000)))
        (assoc ::fetched? (boolean fetched?))
        (dissoc ::confirmations))))

(s/def ::params
  (s/keys :req [::hash ::height ::node ::fetched?]
          :opt [::bits ::chainwork ::difficulty ::merkle-root ::nonce ::size ::time
                ::transaction-count ::median-time ::weight ::version-hex ::stripped-size
                ::version ::next-block ::previous-block]))
(s/def ::item
  (s/keys :req [::id ::hash ::height ::node ::fetched?]
          :opt [::bits ::chainwork ::difficulty ::merkle-root ::nonce ::size ::time
                ::transaction-count ::median-time ::weight ::version-hex ::stripped-size
                ::version  ::next-block ::previous-block]))

(defn idents
  [ids]
  (mapv (fn [id] {::id id}) ids))

(def attributes
  [id bits chainwork
   difficulty hash height
   merkle-root nonce size time version
   transaction-count
   median-time
   next-block
   previous-block
   weight
   version-hex
   stripped-size
   fetched? node])
