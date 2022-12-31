(ns dinsro.components.seed
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [com.fulcrologic.rad.type-support.date-time :as dt]
   [dinsro.actions.authentication :as a.authentication]
   [dinsro.actions.core.nodes :as a.c.nodes]
   [dinsro.actions.core.peers :as a.c.peers]
   [dinsro.actions.core.wallet-addresses :as a.c.wallet-addresses]
   [dinsro.actions.core.wallets :as a.c.wallets]
   [dinsro.actions.ln.nodes :as a.ln.nodes]
   [dinsro.actions.ln.peers :as a.ln.peers]
   [dinsro.actions.ln.remote-nodes :as a.ln.remote-nodes]
   [dinsro.actions.users :as a.users]
   [dinsro.components.config :as config]
   [dinsro.components.seed.accounts]
   [dinsro.components.seed.categories]
   [dinsro.components.seed.core :as cs.core]
   [dinsro.components.seed.ln-node :as cs.ln-node]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.core.addresses :as m.c.addresses]
   [dinsro.model.core.chains :as m.c.chains]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.tx :as m.c.tx]
   [dinsro.model.core.tx-in :as m.c.tx-in]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.core.words :as m.c.words]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.debits :as m.debits]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.navlink :as m.navlink]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.rates :as m.rates]
   [dinsro.model.seed :as seed]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   [dinsro.queries.accounts :as q.accounts]
   [dinsro.queries.categories :as q.categories]
   [dinsro.queries.core.addresses :as q.c.addresses]
   [dinsro.queries.core.chains :as q.c.chains]
   [dinsro.queries.core.mnemonics :as q.c.mnemonics]
   [dinsro.queries.core.networks :as q.c.networks]
   [dinsro.queries.core.nodes :as q.c.nodes]
   [dinsro.queries.core.tx :as q.c.tx]
   [dinsro.queries.core.tx-in :as q.c.tx-in]
   [dinsro.queries.core.wallets :as q.c.wallets]
   [dinsro.queries.core.words :as q.c.words]
   [dinsro.queries.currencies :as q.currencies]
   [dinsro.queries.debits :as q.debits]
   [dinsro.queries.ln.nodes :as q.ln.nodes]
   [dinsro.queries.ln.peers :as q.ln.peers]
   [dinsro.queries.rate-sources :as q.rate-sources]
   [dinsro.queries.rates :as q.rates]
   [dinsro.queries.settings :as q.settings]
   [dinsro.queries.transactions :as q.transactions]
   [dinsro.queries.users :as q.users]
   [dinsro.seed :as seeds]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log]
   [reitit.coercion.spec]
   [tick.alpha.api :as tick]
   [xtdb.api :as xt])
  (:import
   io.grpc.StatusRuntimeException))

(def strict false)

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
    (log/finer :navlink/create {:txes txes})
    (xt/submit-tx node txes)))

(>defn seed-currencies!
  [default-currencies]
  [::cs.core/default-currencies => nil?]
  (log/finer :seed/currencies {:default-currencies default-currencies})
  (doseq [{:keys [code name]} default-currencies]
    (let [currency {::m.currencies/code code
                    ::m.currencies/name name}]
      (q.currencies/create-record currency))))

(defn seed-rate-sources!
  [default-rate-sources]
  (log/finer :seed/rate-sources {:default-rate-sources default-rate-sources})
  (doseq [{:keys [isActive isIdentity code name path url]} default-rate-sources]
    (when-let [currency-id (q.currencies/find-by-code code)]
      (let [rate-source {::m.rate-sources/name      name
                         ::m.rate-sources/currency  currency-id
                         ::m.rate-sources/url       url
                         ::m.rate-sources/active?   isActive
                         ::m.rate-sources/identity? isIdentity
                         ::m.rate-sources/path      path}]
        (q.rate-sources/create-record rate-source)))))

(defn seed-rates!
  [default-rate-sources]
  (log/finer :seed/rates {})
  (doseq [{:keys [name code rates]} default-rate-sources]
    (if-let [currency-id (q.currencies/find-by-code code)]
      (if-let [source-id (q.rate-sources/find-by-currency-and-name currency-id name)]
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
  (log/finer :seed/users {})
  (doseq [{:keys [username password role]} users]
    (let [user (a.authentication/do-register username password)
          user-id (::m.users/id user)]
      (log/info :seed-users!/registered {:user-id user-id})
      (a.users/set-role! user-id role))))

(defn seed-categories!
  [users]
  (log/finer :seed-categories!/starting {})
  (doseq [{:keys [categories username]} users]
    (let [user-id (q.users/find-by-name username)]
      (doseq [{:keys [name]} categories]
        (q.categories/create-record {::m.categories/name name
                                     ::m.categories/user user-id})))))

(defn initialize-ln-node!
  [node-id]
  (log/info :initialize-ln-node!/starting {:node-id node-id})
  (let [node (q.ln.nodes/read-record node-id)]
    (a.ln.nodes/download-cert! node)
    (if-let [macaroon-response (a.ln.nodes/download-macaroon! node)]
      (do
        (log/info :seed-ln-node!/macaroon-downloaded {:macaroon-response macaroon-response})
        (try
          (a.ln.nodes/unlock! node)
          (catch StatusRuntimeException _ex
            (log/info :seed-ln-node!/status-failed {}))
          (catch RuntimeException ex
            (log/info :seed-ln-node!/unlock-failed {:ex ex})))
        (a.ln.nodes/update-info! node)
        (a.ln.peers/fetch-peers! node-id))
      (do
        (log/finer :seed-ln-node!/download-macaroon-failed {})
        (let [initialize-response (a.ln.nodes/initialize! node)]
          (log/info :seed-ln-node!/initialized {:initialize-response initialize-response})
          (a.ln.nodes/download-macaroon! node)
          nil)))))

(>defn seed-ln-node!
  ([user-id node-info]
   [::m.users/id ::cs.ln-node/item => any?]
   (seed-ln-node! user-id node-info false))
  ([user-id node-info initialize-node]
   [::m.users/id ::cs.ln-node/item boolean? => any?]
   (let [{:keys     [name fileserver-host host port mnemonic]
          node-name :node} node-info]
     (log/finer :seed-ln-node!/starting {:node-name node-name})
     (if-let [core-id (q.c.nodes/find-by-name node-name)]
       (if-let [network-id (q.c.networks/find-by-core-node core-id)]
         (let [ln-node {::m.ln.nodes/name            name
                        ::m.ln.nodes/core-node       core-id
                        ::m.ln.nodes/network         network-id
                        ::m.ln.nodes/host            host
                        ::m.ln.nodes/fileserver-host fileserver-host
                        ::m.ln.nodes/port            port
                        ::m.ln.nodes/user            user-id
                        ::m.ln.nodes/mnemonic        mnemonic}
               node-id (q.ln.nodes/create-record ln-node)]
           (log/finer :seed-ln-node!/saved {:node-id node-id})
           (when initialize-node
             (try
               (initialize-ln-node! node-id)
               (catch Exception ex
                 (log/error :seed-ln-node!/init-node-failed {:msg (.getMessage ex)})
                 (when strict (throw (RuntimeException. "init node failed" ex)))))))
         (throw (RuntimeException. "Failed to determine network id")))
       (throw (RuntimeException. (str "Failed to find node: " node-name)))))))

(defn seed-ln-nodes!
  [users]
  (log/finer :seed-ln-nodes!/starting {:users users})
  (doseq [{:keys [username ln-nodes]} users]
    (log/finer :seed-ln-nodes!/processing-user {:username username})
    (let [user-id (q.users/find-by-name username)]
      (doseq [node-info ln-nodes]
        (seed-ln-node! user-id node-info)))))

(defn seed-account!
  [user-id account-data]
  (let [{:keys       [name initial-value source]
         wallet-name :wallet} account-data
        wallet-id             (when wallet-name (q.c.wallets/find-by-user-and-name user-id wallet-name))]
    (if-let [source-id (q.rate-sources/find-by-name source)]
      (let [source      (q.rate-sources/read-record source-id)
            currency-id (::m.rate-sources/currency source)]
        (q.accounts/create-record
         {::m.accounts/name          name
          ::m.accounts/currency      currency-id
          ::m.accounts/user          user-id
          ::m.accounts/initial-value initial-value
          ::m.accounts/wallet        wallet-id
          ::m.accounts/source        source-id}))
      (throw (RuntimeException. "failed to find source")))))

(defn seed-accounts!
  [users]
  (log/finer :seed/accounts {})
  (doseq [{:keys [username accounts]} users]
    (if-let [user-id (q.users/find-by-name username)]
      (doseq [account-data accounts]
        (seed-account! user-id account-data))

      (throw (RuntimeException. "Failed to find user")))))

(defn seed-debit!
  [user-id transaction-id debit-data]
  (log/finer :seed-debit!/starting {:debit-data debit-data})
  (let [{account-name :account
         :keys        [value]} debit-data]
    (log/finer :seed-debit!/parsed {:account-name account-name})
    (if-let [account-id (q.accounts/find-by-user-and-name user-id account-name)]
      (do
        (log/finer :seed-debit!/has-account {})
        (let [params   {::m.debits/account     account-id
                        ::m.debits/transaction transaction-id
                        ::m.debits/value       value}
              debit-id (q.debits/create-record params)]
          (log/finer :seed-debit!/finished {:debit-id debit-id})
          debit-id))
      (throw (RuntimeException. "no account")))))

(defn seed-transaction!
  [user-id transaction-data]
  (let [{:keys [description date debits]} transaction-data
        transaction                       {::m.transactions/date        date
                                           ::m.transactions/description description}]
    (log/finer :seed-transaction!/prepared {:transaction transaction})
    (let [transaction-id (q.transactions/create-record transaction)]
      (log/finer :seed-transaction!/created {:transaction-id transaction-id})
      (doseq [debit debits]
        (seed-debit! user-id transaction-id debit))
      transaction-id)))

(defn seed-transactions!
  [users]
  (log/finer :seed/txes {})
  (doseq [{:keys [username transactions]} users]
    (if-let [user-id (q.users/find-by-name username)]
      (doseq [transaction transactions]
        (seed-transaction! user-id transaction))
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
        ln-peers     (count (q.ln.peers/index-ids))]
    (log/finer :report
               {:users        users
                :categories   categories
                :currencies   currencies
                :rate-sources rate-sources
                :rates        rates
                :accounts     accounts
                :transactions transactions
                :ln-nodes     ln-nodes
                :ln-peers     ln-peers})))

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
    (if-let [node-id (q.c.nodes/find-by-name node-name)]
      (let [tx    (assoc tx ::m.c.tx/node node-id)
            tx-id (q.c.tx/create-record tx)]
        (doseq [tx-in in]
          (let [tx-in (assoc tx-in ::m.c.tx-in/transaction tx-id)]
            (q.c.tx-in/create-record tx-in))))
      (throw (RuntimeException. "Can't find node")))))

(comment
  (seed-core-txes!))

(defn seed-wallet-address!
  [wallet-id]
  (log/finer :seed-wallet-address!/starting {:wallet-id wallet-id})
  (let [wallet (q.c.wallets/read-record wallet-id)]
    (doseq [i (range 20)]
      (log/finer :seed-wallet-address!/iterating {:i i})
      (let [address (a.c.wallets/get-address wallet i)]
        (log/finer :seed-wallet-address!/process-address {:address address})
        (a.c.wallet-addresses/register-address! wallet address i)))))

(defn seed-wallet!
  [user-id wallet-data]
  (log/finer :seed-wallets!/process-wallet {:wallet-data wallet-data})
  (let [{:keys     [name seed path]
         node-name :node} wallet-data
        node-id           (q.c.nodes/find-by-name node-name)
        node              (q.c.nodes/read-record node-id)
        network-id        (::m.c.nodes/network node)
        mnemonic-id       (q.c.mnemonics/create-record {})
        wallet-id         (q.c.wallets/create-record
                           {::m.c.wallets/name       name
                            ::m.c.wallets/derivation path
                            ::m.c.wallets/network    network-id
                            ::m.c.wallets/mnemonic   mnemonic-id
                            ::m.c.wallets/user       user-id})]
    (doseq [[i word] (map-indexed vector seed)]
      (log/finer :seed-wallets!/process-word {:word word :i i})
      (let [props {::m.c.words/mnemonic mnemonic-id
                   ::m.c.words/word     word
                   ::m.c.words/position (inc i)}]
        (q.c.words/create-record props)))
    (seed-wallet-address! wallet-id)))

(defn seed-wallets!
  [users]
  (doseq [user-info users]
    (let [{:keys [username]} user-info
          user-id            (q.users/find-by-name username)]
      (log/debug :seed-wallets!/starting {:username username :user-id user-id})
      (doseq [wallet (get user-info :wallets [])]
        (seed-wallet! user-id wallet)))))

(defn seed-addresses!
  [addresses]
  (doseq [address addresses]
    (q.c.addresses/create-record {::m.c.addresses/address address})))

(comment

  (tap> (ds/gen-key ::seed-data))

  nil)

(defn seed-chains!
  [chains]
  (log/finer :seed-chains!/starting {:chains chains})
  (doseq [chain chains]
    (q.c.chains/create-record {::m.c.chains/name chain})))

(defn seed-networks!
  [networks]
  (log/finer :seed-networks!/starting {:networks networks})
  (doseq [[chain-name network-names] networks]
    (if-let [chain-id (q.c.chains/find-by-name chain-name)]
      (do
        (log/finer :seed-networks!/found {:chain-id chain-id :chain-name chain-name})
        (doseq [network-name network-names]
          (log/finer :seed-networks!/processing-network {:network-name network-name})
          (q.c.networks/create-record
           {::m.c.networks/name network-name
            ::m.c.networks/chain chain-id})))

      (do
        (log/finer :seed-networks!/not-foind {:chain-name chain-name})
        nil))))

(defn seed-core-nodes!
  "Create core nodes"
  [core-node-data]
  (log/fine :seed-core-nodes!/starting {:core-node-data core-node-data})
  (doseq [data core-node-data]
    (log/finer :seed-core-nodes!/processing-node {:data data})
    (let [{chain-name       :chain
           network-name     :network
           ::m.c.nodes/keys [host port rpcuser rpcpass name]} data]
      (if-let [network-id (q.c.networks/find-by-chain-and-network chain-name network-name)]
        (let [params  {::m.c.nodes/name    name
                       ::m.c.nodes/host    host
                       ::m.c.nodes/port    port
                       ::m.c.nodes/network network-id
                       ::m.c.nodes/rpcuser rpcuser
                       ::m.c.nodes/rpcpass rpcpass}
              node-id (q.c.nodes/create-record params)
              node    (q.c.nodes/read-record node-id)]
          (try
            (a.c.nodes/fetch! node)
            (catch Exception ex
              (log/error :seed-core-nodes!/failed {:ex ex})
              (when strict (throw (RuntimeException. "seed core nodes failed"))))))
        (do
          (log/error :seed-core-nodes!/network-not-found {:chain-name chain-name :network-name network-name})
          (throw (RuntimeException. "Failed to find chain")))))))

(defn seed-core-peers!
  "Create peers between core nodes"
  [core-node-data]
  (log/finer :seed-core-peers!/starting {:core-node-data core-node-data})
  (doseq [node-data core-node-data]
    (let [peers       (:peers node-data)
          target-name (::m.c.nodes/name node-data)
          target-peer (q.c.nodes/read-record (q.c.nodes/find-by-name target-name))]
      (a.c.peers/fetch-peers! target-peer)
      (doseq [peer-name peers]
        (let [remote-peer (q.c.nodes/read-record (q.c.nodes/find-by-name peer-name))
              remote-host (::m.c.nodes/host remote-peer)
              remote-uri  (str "http://" remote-host)]
          (if (a.c.peers/has-peer? target-peer remote-uri)
            (log/finer :seed-core-peers!/has-peer {})
            (try
              (log/finer :seed-core-peers!/no-peer {})
              (a.c.peers/add-peer! target-peer remote-uri)
              (a.c.peers/fetch-peers! target-peer)
              (catch Exception ex
                (log/error :seed-core-peers!/no-peer-failed {:ex ex})
                (when strict (throw (RuntimeException. "Failed to add peer" ex)))))))))))

(defn seed-remote-nodes-remote-node!
  [user-id node-id remote-node-data]
  (log/finer
   :seed-remote-nodes-remote-node!/starting
   {:user-id          user-id
    :node-id          node-id
    :remote-node-data remote-node-data})
  (let [{:keys [pubKey host]} remote-node-data]
    (a.ln.remote-nodes/register-node! node-id pubKey host)))

(defn seed-remote-nodes-node!
  [user-id ln-node-data]
  (log/finer :seed-remote-nodes-node!/starting {:user-id user-id :ln-node-data ln-node-data})
  (let [node-name (:name ln-node-data)
        node-id (q.ln.nodes/find-by-user-and-name user-id node-name)]
    (log/finer :seed-remote-nodes-node!/found-node-id {:node-id node-id})
    (let [remote-nodes-data (:remote-nodes ln-node-data)]
      (doseq [remote-node-data remote-nodes-data]
        (seed-remote-nodes-remote-node! user-id node-id remote-node-data)))))

(defn seed-remote-nodes-user!
  [user-data]
  (log/finer :seed-remote-nodes-user!/starting {:user-data user-data})
  (let [username      (:username user-data)
        user-id       (q.users/find-by-name username)
        ln-nodes-data (:ln-nodes user-data)]
    (doseq [ln-node-data ln-nodes-data]
      (seed-remote-nodes-node! user-id ln-node-data))))

(defn seed-remote-nodes!
  [users-data]
  (log/finer :seed-remote-nodes!/starting {:users-data users-data})
  (doseq [user-data users-data]
    (seed-remote-nodes-user! user-data)))

(>defn seed-db!
  [seed-data]
  [::cs.core/seed-data => any?]
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

    (try
      (seed-core-nodes! core-node-data)
      (seed-core-peers! core-node-data)
      (catch Exception ex
        (log/error :seed-db!/core-nodes-failed {:ex ex})
        (when strict (throw (RuntimeException. "seed core nodes failed" ex)))))

    (seed-currencies! default-currencies)
    (seed-rate-sources! default-rate-sources)
    (seed-rates! default-rate-sources)

    (seed-users! users)
    (seed-categories! users)
    (seed-wallets! users)
    (seed-accounts! users)
    (seed-transactions! users)

    (seed-ln-nodes! users)
    ;; (seed-addresses! [])
    ;; (seed-remote-nodes! users)

    (log/finer :seed/finished {})
    (item-report)))

(>defn get-seed-data
  []
  [=> ::cs.core/seed-data]
  (seeds/get-seed-data))

(def seeded-key ::seeded)

(defn seed!
  []
  (if (config/config ::enabled)
    (if (q.settings/get-setting seeded-key)
      (log/finer :seed!/seeded {})
      (do
        (log/finer :seed!/not-seeded {})
        (let [seed-data (get-seed-data)]
          (try
            (seed-db! seed-data)
            (catch Exception ex
              (log/error :seed!/failed {:ex ex})
              (when strict (throw (RuntimeException. "seed failed" ex)))))
          (q.settings/set-setting seeded-key true))))
    (log/finer :seed!/not-enabled {})))

(comment

  (q.settings/set-setting seeded-key :foo)
  (q.settings/get-setting seeded-key)

  nil)
