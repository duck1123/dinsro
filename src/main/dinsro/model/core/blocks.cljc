(ns dinsro.model.core.blocks
  (:refer-clojure :exclude [hash time])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.client.converters.get-block-result :as c.c.get-block-result]
   [dinsro.model.core.networks :as m.c.networks]
   [lambdaisland.glogc :as log]
   [tick.alpha.api :as tick]))

(s/def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::bits number?)
(defattr bits ::bits :long
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::chainwork string?)
(defattr chainwork ::chainwork :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::median-time inst?)
(defattr median-time ::median-time :instant
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
(defattr time ::time :instant
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

(s/def ::network uuid?)
(defattr network ::network :ref
  {ao/identities       #{::id}
   ao/target           ::m.c.networks/id
   ao/schema           :production
   ::report/column-EQL {::network [::m.c.networks/id ::m.c.networks/name]}})

(s/def ::fetched? boolean?)
(defattr fetched? ::fetched? :boolean
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::unprepared-params
  (s/keys
   :req [::network ::fetched?
         ::c.c.get-block-result/bits
         ::c.c.get-block-result/chainwork
         ::c.c.get-block-result/confirmations
         ::c.c.get-block-result/difficulty
         ::c.c.get-block-result/hash
         ::c.c.get-block-result/height
         ::c.c.get-block-result/median-time
         ::c.c.get-block-result/merkle-root
         ::c.c.get-block-result/next-block-hash]))

(s/def ::params
  (s/keys :req [::hash ::height ::fetched? ::network]
          :opt [::bits ::chainwork ::difficulty ::merkle-root ::nonce ::size ::time
                ::transaction-count ::median-time ::weight ::version-hex ::stripped-size
                ::version ::next-block ::previous-block]))

(>defn prepare-params
  [params]
  [::unprepared-params => ::params]
  (log/finer :prepare-params/preparing {:params params})
  (let [{:dinsro.client.converters.get-block-result/keys
         [bits hash height chainwork difficulty merkle-root
          nonce size time tx median-time weight version-hex
          stripped-size version]
         ::keys [fetched? network]} params
        time-inst                   (some-> time (* 1000) tick/instant)
        transaction-count           (count tx)
        median-time-inst            (some-> median-time (* 1000) tick/instant)
        record                      {::hash              hash
                                     ::height            height
                                          ;; ::node              node
                                     ::fetched?          fetched?
                                     ::bits              bits
                                     ::chainwork         chainwork
                                     ::difficulty        difficulty
                                     ::merkle-root       merkle-root
                                     ::nonce             nonce
                                     ::network           network
                                     ::size              size
                                     ::time              time-inst
                                     ::transaction-count transaction-count
                                     ::median-time       median-time-inst
                                     ::weight            weight
                                     ::version-hex       version-hex
                                     ::stripped-size     stripped-size
                                     ::version           version}]
    (log/finer :prepare-params/finished {:record record})
    record))

(s/def ::item
  (s/keys :req [::id ::hash ::height ::network ::fetched?]
          :opt [::bits ::chainwork ::difficulty ::merkle-root ::nonce ::size ::time
                ::transaction-count ::median-time ::weight ::version-hex ::stripped-size
                ::version  ::next-block ::previous-block]))

(s/def ::ident (s/keys :req [::id]))
(>defn ident [id] [::id => ::ident] {::id id})
(>defn idents [ids] [(s/coll-of ::id) => (s/coll-of ::ident)] (mapv ident ids))

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
   fetched? network])
