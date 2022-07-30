(ns dinsro.model.core.peers
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.core.nodes :as m.c.nodes]
   [lambdaisland.glogc :as log]))

(s/def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::address-bind string?)
(defattr address-bind ::address-bind :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::subver string?)
(defattr subver ::subver :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::addr string?)
(defattr addr ::addr :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::peer-id number?)
(defattr peer-id ::peer-id :long
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::connection-type string?)
(defattr connection-type ::connection-type :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::node uuid?)
(defattr node ::node :ref
  {ao/identities       #{::id}
   ao/target           ::m.c.nodes/id
   ao/schema           :production
   ::report/column-EQL {::node [::m.c.nodes/id ::m.c.nodes/name]}})

(s/def ::params
  (s/keys :req [::address-bind
                ::subver
                ::addr
                ::peer-id
                ::connection-type

                ::node]))

(s/def ::item
  (s/keys :req [::id ::address-bind
                ::subver
                ::addr
                ::peer-id
                ::connection-type

                ::node]))

(def rename-map
  {:addrbind ::address-bind
   :subver ::subver
   :connection_type ::connection-type
   :addr ::addr
   :id ::peer-id})

(>defn prepare-params
  [params]
  [any? => ::params]
  ;; (-> params
  ;;     (set/rename-keys rename-map))
  (log/info :prepare-params/starting {:params params})
  (let [address-bind (get-in params [:network-info :addr-bind])
        prepared     {::address-bind    address-bind
                      ::subver          (:subver params)
                      ::addr            (get-in params [:network-info :addr])
                      ::peer-id         (:id params)
                      ::connection-type (:connection-type params)
                      ::node            (::node params)}]
    (log/info :prepare-params/finished {:prepared prepared})
    prepared))

(>defn ident
  [id]
  [::id => (s/keys)]
  {::id id})

(>defn ident-item
  [{::keys [id]}]
  [::item => (s/keys)]
  (ident id))

(>defn idents
  [ids]
  [(s/coll-of ::id) => (s/coll-of (s/keys))]
  (mapv ident ids))

(def attributes
  [id address-bind subver addr peer-id connection-type node])

(comment
  (def example-peer
    {:bip152_hb_to      false,
     :relaytxes         true,
     :minping           0.001998,
     :permissions       [],
     :addrbind          "10.42.0.111:52264",
     :conntime          1641681512,
     :bytessent_per_msg {:ping        32,
                         :pong        32,
                         :sendheaders 24,
                         :sendcmpct   66,
                         :wtxidrelay  24,
                         :getheaders  93,
                         :verack      24,
                         :sendaddrv2  24,
                         :version     126,
                         :feefilter   32,
                         :getaddr     24},
     :addr_rate_limited 0,
     :synced_blocks     -1,
     :bytesrecv_per_msg {:ping        32,
                         :pong        32,
                         :sendheaders 24,
                         :sendcmpct   66,
                         :wtxidrelay  24,
                         :getheaders  93,
                         :headers     25,
                         :verack      24,
                         :sendaddrv2  24,
                         :version     126,
                         :feefilter   32},
     :startingheight    0,
     :subver            "/Satoshi:22.0.0/",
     :inflight          [],
     :last_block        0,
     :addr              "bitcoin.bitcoin-bob",
     :bip152_hb_from    false,
     :connection_type   "manual",
     :bytessent         501,
     :last_transaction  0,
     :id                0,
     :addr_processed    0,
     :minfeefilter      0.09170997,
     :pingtime          0.001998,
     :timeoffset        0,
     :synced_headers    -1,
     :inbound           false,
     :network           "not_publicly_routable",
     :bytesrecv         502,
     :version           70016,
     :lastsend          1641681512,
     :lastrecv          1641681512,
     :servicesnames     ["NETWORK" "WITNESS" "NETWORK_LIMITED"],
     :services          "0000000000000409"})

  (prepare-params (assoc example-peer ::node (new-uuid)))

  nil)
