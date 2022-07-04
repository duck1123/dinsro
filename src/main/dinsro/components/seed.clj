(ns dinsro.components.seed
  (:require
   [clojure.spec.alpha :as s]
   [clojure.set :as set]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [com.fulcrologic.rad.type-support.date-time :as dt]
   [xtdb.api :as xt]
   [dinsro.actions.authentication :as a.authentication]
   [dinsro.actions.core.nodes :as a.c.nodes]
   [dinsro.actions.core.tx :as a.c.tx]
   [dinsro.actions.ln.nodes :as a.ln.nodes]
   [dinsro.actions.ln.peers :as a.ln.peers]
   [dinsro.actions.rates :as a.rates]
   [dinsro.components.config :as config]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.core.addresses :as m.c.addresses]
   [dinsro.model.core.chains :as m.c.chains]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.tx :as m.c.tx]
   [dinsro.model.core.tx-in :as m.c.tx-in]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.ln.info :as m.ln.info]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.ln.peers :as m.ln.peers]
   [dinsro.model.ln.transactions :as m.ln.tx]
   [dinsro.model.navlink :as m.navlink]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.rates :as m.rates]
   [dinsro.model.seed :as seed]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.core.words :as m.c.words]
   [dinsro.queries.accounts :as q.accounts]
   [dinsro.queries.categories :as q.categories]
   [dinsro.queries.core.addresses :as q.c.addresses]
   [dinsro.queries.core.chains :as q.c.chains]
   [dinsro.queries.core.networks :as q.c.networks]
   [dinsro.queries.core.nodes :as q.c.nodes]
   [dinsro.queries.core.tx :as q.c.tx]
   [dinsro.queries.core.tx-in :as q.c.tx-in]
   [dinsro.queries.currencies :as q.currencies]
   [dinsro.queries.ln.nodes :as q.ln.nodes]
   [dinsro.queries.ln.peers :as q.ln.peers]
   [dinsro.queries.ln.transactions :as q.ln.tx]
   [dinsro.queries.rate-sources :as q.rate-sources]
   [dinsro.queries.rates :as q.rates]
   [dinsro.queries.settings :as q.settings]
   [dinsro.queries.transactions :as q.transactions]
   [dinsro.queries.users :as q.users]
   [dinsro.queries.core.wallets :as q.c.wallets]
   [dinsro.queries.core.words :as q.c.words]
   [dinsro.seed :as seeds]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log]
   [reitit.coercion.spec]
   [tick.alpha.api :as tick]))

(s/def ::default-rate-source (s/keys))
(s/def ::default-rate-sources (s/coll-of ::default-rate-source))

(s/def ::name string?)
(s/def ::code string?)

(s/def ::default-currency (s/keys :req-un [::name ::code]))
(s/def ::default-currencies (s/coll-of ::default-currency))

(s/def ::category (s/keys :req-un [::name]))
(s/def ::username string?)
(s/def ::password string?)
(s/def ::categories (s/coll-of ::category))

(s/def ::source string?)
(s/def ::initial-value number?)
(s/def ::account (s/keys :req-un [::name ::initial-value ::source]))
(s/def ::accounts (s/coll-of ::account))

(s/def ::node string?)
(s/def ::word string?)
(s/def ::path string?)
(s/def ::seed (s/coll-of ::word))
(s/def ::label string?)
(s/def ::blockheight number?)
(s/def ::descriptor string?)
(s/def ::wallet (s/keys :req-un [::label ::blockheight ::name ::descriptor ::node ::path]))
(s/def ::wallets (s/coll-of ::wallet))
(s/def ::user-data (s/keys :req-un [::username ::password ::categories ::accounts]))
(s/def ::users (s/coll-of ::user-data))
(s/def ::core-node-data-item (s/keys :req [::m.c.nodes/name ::m.c.nodes/host
                                           ::m.c.nodes/port
                                           ::m.c.nodes/rpcuser
                                           ::m.c.nodes/rpcpass]))
(s/def ::core-node-data (s/coll-of ::core-node-data-item))
(s/def ::default-timezone string?)
(s/def ::seed-data
  (s/keys :req-un [::default-timezone
                   ::core-node-data
                   ::users
                   ::default-currencies
                   ::default-rate-sources]))

(defn create-navlinks!
  []
  (let [node (:main c.xtdb/xtdb-nodes)
        add  (fnil conj [])
        data (reduce
              (fn [data link]
                (let [[id name href target] link]
                  (update data :navlinks add (seed/new-navlink id name href target))))
              {} m.navlink/links)
        txes (->> data
                  vals
                  flatten
                  (mapv #(vector ::xt/put %)))]
    (log/info :navlink/create {:txes txes})
    (xt/submit-tx node txes)))

(>defn seed-rate!
  [_user-id currency-id source-id {:keys [date rate]}]
  [::m.users/id ::m.currencies/id ::m.rate-sources/id any? => any?]
  (let [params {::m.rates/currency currency-id
                ::m.rates/date     date
                ::m.rates/rate     rate}]
    (log/info :rate/create {:params params})
    (a.rates/add-rate source-id params)))

(defn seed-peer!
  [node-id {:keys [ref] :as peer}]
  (let [[target-username target-node] ref]
    (if-let [target-user-id (q.users/find-eid-by-name target-username)]
      (if-let [target-id (q.ln.nodes/find-id-by-user-and-name target-user-id target-node)]
        (let [{:keys [host identity-pubkey]} (q.ln.nodes/read-record target-id)
              params                         (-> peer
                                                 (assoc ::m.ln.peers/address host)
                                                 (assoc ::m.ln.peers/pubkey identity-pubkey)
                                                 (set/rename-keys m.ln.peers/rename-map))]
          (log/info :peer/create {:params params})
          (q.ln.peers/add-peer! node-id params))
        (throw (RuntimeException. (str "no ref: " ref))))
      (throw (RuntimeException. "no user")))))

(>defn seed-currencies!
  [default-currencies]
  [::default-currencies => any?]
  (log/info :seed/currencies {:default-currencies default-currencies})
  (doseq [{:keys [code name]} default-currencies]
    (let [currency {::m.currencies/code code
                    ::m.currencies/name name}]
      (q.currencies/create-record currency))))

(comment

  (ds/gen-key ::default-currencies)

  nil)

(defn seed-rate-sources!
  [default-rate-sources]
  (log/info :seed/rate-sources {:default-rate-sources default-rate-sources})
  (doseq [{:keys [isActive isIdentity code name path url]} default-rate-sources]
    (when-let [currency-id (q.currencies/find-eid-by-code code)]
      (let [rate-source {::m.rate-sources/name      name
                         ::m.rate-sources/currency  currency-id
                         ::m.rate-sources/url       url
                         ::m.rate-sources/active?   isActive
                         ::m.rate-sources/identity? isIdentity
                         ::m.rate-sources/path      path}]
        (q.rate-sources/create-record rate-source)))))

(defn seed-rates!
  [default-rate-sources]
  (log/info :seed/rates {})
  (doseq [{:keys [name code rates]} default-rate-sources]
    (if-let [currency-id (q.currencies/find-eid-by-code code)]
      (if-let [source-id (q.rate-sources/find-id-by-currency-and-name currency-id name)]
        (doseq [{:keys [date rate]} rates]
          (let [rate {::m.rates/currency currency-id
                      ::m.rates/date     date
                      ::m.rates/source   source-id
                      ::m.rates/rate     rate}]
            (q.rates/create-record rate)))
        (throw (RuntimeException. "Failed to find source")))
      (throw (RuntimeException. "Failed to find currency")))))

(defn seed-users!
  [users]
  (log/info :seed/users {})
  (doseq [{:keys [username password]} users]
    (a.authentication/do-register username password)))

(defn seed-categories!
  [users]
  (log/info :seed-categories!/starting {})
  (doseq [{:keys [categories username]} users]
    (let [user-id (q.users/find-eid-by-name username)]
      (doseq [{:keys [name]} categories]
        (q.categories/create-record {::m.categories/name name
                                     ::m.categories/user user-id})))))

(defn seed-ln-nodes!
  [users]
  (log/info :seed-ln-nodes!/starting {:users users})
  (doseq [{:keys [username ln-nodes]} users]
    (let [user-id (q.users/find-eid-by-name username)]
      (doseq [{:keys     [name host port mnemonic] :as info
               node-name :node} ln-nodes]
        (if-let [core-id (q.c.nodes/find-id-by-name node-name)]
          (let [ln-node {::m.ln.nodes/name      name
                         ::m.ln.nodes/core-node core-id
                         ::m.ln.nodes/host      host
                         ::m.ln.nodes/port      port
                         ::m.ln.nodes/user      user-id
                         ::m.ln.nodes/mnemonic  mnemonic}
                node-id (q.ln.nodes/create-record ln-node)
                info    (set/rename-keys info m.ln.info/rename-map)]
            (a.ln.nodes/save-info! node-id info)
            (try
              (let [node (q.ln.nodes/read-record node-id)]
                (a.ln.nodes/download-cert! node)
                (a.ln.nodes/download-macaroon! node)
                (a.ln.nodes/update-info! node)
                (a.ln.peers/fetch-peers! node-id))
              (catch Exception ex
                (log/error :seed-ln-nodes!/init-node-failed {:ex ex}))))
          (throw (RuntimeException. (str "Failed to find node: " node-name))))))))

(defn seed-ln-peers!
  [users]
  (log/info :seed-ln-peers!/starting {})
  (doseq [{:keys [username ln-nodes]} users]
    (let [user-id (q.users/find-eid-by-name username)]
      (doseq [{:keys [name peers]} ln-nodes]
        (if-let [node-id (q.ln.nodes/find-id-by-user-and-name user-id name)]
          (doseq [{[target-username target-node] :ref :as peer} peers]
            (if-let [target-user-id (q.users/find-eid-by-name target-username)]
              (if-let [target-id (q.ln.nodes/find-id-by-user-and-name target-user-id target-node)]
                (let [{:keys [host identity-pubkey]} (q.ln.nodes/read-record target-id)
                      peer                           (-> peer
                                                         (assoc ::m.ln.peers/address host)
                                                         (assoc ::m.ln.peers/pubkey identity-pubkey)
                                                         (set/rename-keys m.ln.peers/rename-map))]
                  (q.ln.peers/add-peer! node-id peer))
                (throw (RuntimeException. (str "no target: " target-node))))
              (throw (RuntimeException. "no user"))))
          (throw (RuntimeException. "no node")))))))

(defn seed-ln-txes!
  [users]
  (log/info :seed/ln.txes {})
  (doseq [{:keys [ln-nodes username]} users]
    (if-let [user-id (q.users/find-eid-by-name username)]
      (doseq [{:keys [name txes]} ln-nodes]
        (if-let [ln-node-id (q.ln.nodes/find-id-by-user-and-name user-id name)]
          (if-let [ln-node (q.ln.nodes/read-record ln-node-id)]
            (if-let [core-node-id (::m.ln.nodes/core-node ln-node)]
              (doseq [tx txes]
                (let [tx           (set/rename-keys tx m.ln.tx/rename-map)
                      tx           (assoc tx ::m.ln.tx/node ln-node-id)
                      tx-hash      (::m.ln.tx/tx-hash tx)
                      block-hash   (::m.ln.tx/block-hash tx)
                      block-height (::m.ln.tx/block-height tx)
                      core-tx-id   (a.c.tx/register-tx core-node-id block-hash block-height tx-hash)
                      tx           (assoc tx ::m.ln.tx/core-tx core-tx-id)]
                  (q.ln.tx/add-tx ln-node-id tx)))
              (throw (RuntimeException. "Node does not contain a core node id")))
            (throw (RuntimeException. "Failed to read ln node")))
          (throw (RuntimeException. "Failed to find ln node"))))
      (throw (RuntimeException. "Failed to find ln tx")))))

(defn seed-accounts!
  [users]
  (log/info :seed/accounts {})
  (doseq [{:keys [username accounts]} users]
    (if-let [user-id (q.users/find-eid-by-name username)]
      (doseq [{:keys [name initial-value source]} accounts]
        (if-let [source-id (q.rate-sources/find-eid-by-name source)]
          (let [source      (q.rate-sources/read-record source-id)
                currency-id (::m.rate-sources/currency source)]
            (q.accounts/create-record
             {::m.accounts/name          name
              ::m.accounts/currency      currency-id
              ::m.accounts/user          user-id
              ::m.accounts/initial-value initial-value
              ::m.accounts/source        source-id}))
          (throw (RuntimeException. "failed to find source"))))
      (throw (RuntimeException. "Failed to find user")))))

(defn seed-transactions!
  [users]
  (log/info :seed/txes {})
  (doseq [{:keys [accounts username]} users]
    (if-let [user-id (q.users/find-eid-by-name username)]
      (doseq [{:keys [name transactions]} accounts]
        (if-let [account-id (q.accounts/find-id-by-user-and-name user-id name)]
          (doseq [{:keys [description date value]} transactions]
            (let [transaction {::m.transactions/date        date
                               ::m.transactions/description description
                               ::m.transactions/account     account-id
                               ::m.transactions/value       value}]
              (q.transactions/create-record transaction)))
          (throw (RuntimeException. "Failed fo find account"))))
      (throw (RuntimeException. "Failed to find user")))))

(defn item-report
  []
  (let [users        (count (q.users/index-ids))
        categories   (count (q.categories/index-ids))
        currencies   (count (q.currencies/index-ids))
        rate-sources (count (q.rate-sources/index-ids))
        rates        (count (q.rates/index-ids))
        accounts     (count (q.accounts/index-ids))
        transactions (count (q.transactions/index-ids))
        ln-nodes     (count (q.ln.nodes/index-ids))
        ln-peers     (count (q.ln.peers/index-ids))
        ln-txes      (count (q.ln.tx/index-ids))]
    (log/info :report
              {:users        users
               :categories   categories
               :currencies   currencies
               :rate-sources rate-sources
               :rates        rates
               :accounts     accounts
               :transactions transactions
               :ln-nodes     ln-nodes
               :ln-peers     ln-peers
               :ln-txes      ln-txes})))

(defn mock-tx
  [o]
  (merge
   {:node          "main"
    :blockHash     "block hash"
    :blockTime     (tick/instant)
    :confirmations 1
    :hash          "hash"
    :hex           "hex"
    :lockTime      0
    :size          0
    :time          (tick/instant)
    :version       1
    :vsize         1
    :in            [{:scriptSig {}
                     :sequence  0}]}
   o))

(def core-txes
  [(mock-tx {})
   (mock-tx {})])

(defn seed-core-txes!
  []
  (doseq [{:as       tx
           :keys     [in]
           node-name :node} core-txes]
    (if-let [node-id (q.c.nodes/find-id-by-name node-name)]
      (let [tx    (assoc tx ::m.c.tx/node node-id)
            tx-id (q.c.tx/create-record tx)]
        (doseq [tx-in in]
          (let [tx-in (assoc tx-in ::m.c.tx-in/transaction tx-id)]
            (q.c.tx-in/create-record tx-in))))
      (throw (RuntimeException. "Can't find node")))))

(comment
  (seed-core-txes!))

(defn seed-wallets!
  [users]
  (doseq [user-info users]
    (let [{:keys [username]} user-info
          user-id            (q.users/find-eid-by-name username)]
      (log/info :seed/wallet {:user-info user-info :user-id user-id})
      (doseq [wallet (get user-info :wallets [])]
        (let [{:keys     [name seed path]
               node-name :node} wallet
              node-id           (q.c.nodes/find-id-by-name node-name)
              wallet-id         (q.c.wallets/create-record
                                 {::m.c.wallets/name       name
                                  ::m.c.wallets/derivation path
                                  ::m.c.wallets/node       node-id
                                  ::m.c.wallets/user       user-id})]
          (doseq [[i word] (map-indexed vector seed)]
            (let [props {::m.c.words/wallet   wallet-id
                         ::m.c.words/word     word
                         ::m.c.words/position (inc i)}]
              (q.c.words/create-record props))))))))

(defn seed-addresses!
  [addresses]
  (doseq [address addresses]
    (q.c.addresses/create-record {::m.c.addresses/address address})))

(comment

  (tap> (ds/gen-key ::seed-data))

  nil)

(defn seed-chains!
  [chains]
  (log/info :seed-chains!/starting {:chains chains})
  (doseq [chain chains]
    (q.c.chains/create-record {::m.c.chains/name chain})))

(defn seed-networks!
  [networks]
  (log/info :seed-networks!/starting {:networks networks})
  (doseq [[chain-name network-names] networks]
    (if-let [chain-id (q.c.chains/find-id-by-name chain-name)]
      (do
        (log/info :seed-networks!/found {:chain-id chain-id :chain-name chain-name})
        (doseq [network-name network-names]
          (log/info :seed-networks!/processing-network {:network-name network-name})
          (q.c.networks/create-record
           {::m.c.networks/name network-name
            ::m.c.networks/chain chain-id})))

      (do
        (log/info :seed-networks!/not-foind {:chain-name chain-name})
        nil))))

(defn seed-core-nodes!
  [core-node-data]
  (doseq [data core-node-data]
    (let [node-id (q.c.nodes/create-record data)
          node (q.c.nodes/read-record node-id)]
      (try
        (a.c.nodes/fetch! node)
        (catch Exception ex
          (log/error :seed-core-nodes!/failed {:ex ex}))))))

(>defn seed-db!
  [seed-data]
  [::seed-data => any?]
  (let [{:keys [default-timezone
                core-node-data
                users
                default-chains
                default-currencies
                default-networks
                default-rate-sources]} seed-data]
    (create-navlinks!)
    (dt/set-timezone! default-timezone)

    (seed-chains! default-chains)
    (seed-networks! default-networks)
    (seed-core-nodes! core-node-data)

    #_(seed-core-txes!)

    (seed-currencies! default-currencies)
    (seed-rate-sources! default-rate-sources)
    (seed-rates! default-rate-sources)
    (seed-users! users)
    (seed-categories! users)
    (seed-accounts! users)
    (seed-transactions! users)
    (seed-ln-nodes! users)
    (seed-wallets! users)
    (seed-addresses! [])
    ;; (seed-ln-peers! users)
    ;; (seed-ln-txes! users)

    (log/info :seed/finished {})
    (item-report)))

(defn get-seed-data
  []
  {}
  (seeds/get-seed-data))

(def seeded-key ::seeded)

(defn seed!
  []
  (if (config/config ::enabled)
    (if (q.settings/get-setting seeded-key)
      (log/info :seed/seeded {})
      (do
        (log/info :seed/not-seeded {})
        (let [seed-data (get-seed-data)]
          (try
            (seed-db! seed-data)
            (catch Exception ex
              (log/error :seed/failed {:ex ex})))
          (q.settings/set-setting seeded-key true))))
    (log/info :seed!/not-enabled {})))

(comment

  (q.settings/set-setting seeded-key :foo)
  (q.settings/get-setting seeded-key)

  nil)
