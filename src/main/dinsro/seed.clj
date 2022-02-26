(ns dinsro.seed
  (:require
   [clojure.set :as set]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [com.fulcrologic.rad.type-support.date-time :as dt]
   [xtdb.api :as xt]
   [dinsro.actions.authentication :as a.authentication]
   [dinsro.actions.core-tx :as a.core-tx]
   [dinsro.actions.ln-nodes :as a.ln-nodes]
   [dinsro.actions.rates :as a.rates]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.core-address :as m.core-address]
   [dinsro.model.core-nodes :as m.core-nodes]
   [dinsro.model.core-tx :as m.core-tx]
   [dinsro.model.core-tx-in :as m.core-tx-in]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.ln-info :as m.ln-info]
   [dinsro.model.ln-nodes :as m.ln-nodes]
   [dinsro.model.ln-peers :as m.ln-peers]
   [dinsro.model.ln-transactions :as m.ln-tx]
   [dinsro.model.navlink :as m.navlink]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.rates :as m.rates]
   [dinsro.model.seed :as seed]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   [dinsro.model.wallets :as m.wallets]
   [dinsro.model.words :as m.words]
   [dinsro.queries.accounts :as q.accounts]
   [dinsro.queries.categories :as q.categories]
   [dinsro.queries.core-address :as q.core-address]
   [dinsro.queries.core-nodes :as q.core-nodes]
   [dinsro.queries.core-tx :as q.core-tx]
   [dinsro.queries.core-tx-in :as q.core-tx-in]
   [dinsro.queries.currencies :as q.currencies]
   [dinsro.queries.ln-nodes :as q.ln-nodes]
   [dinsro.queries.ln-peers :as q.ln-peers]
   [dinsro.queries.ln-transactions :as q.ln-tx]
   [dinsro.queries.rate-sources :as q.rate-sources]
   [dinsro.queries.rates :as q.rates]
   [dinsro.queries.transactions :as q.transactions]
   [dinsro.queries.users :as q.users]
   [dinsro.queries.wallets :as q.wallets]
   [dinsro.queries.words :as q.words]
   [reitit.coercion.spec]
   [taoensso.timbre :as log]
   [tick.alpha.api :as tick]))

(def default-password m.users/default-password)
(def default-timezone "America/Detroit")

(defn ->inst
  [s]
  (tick/instant (tick/in (tick/date-time s) default-timezone)))

(defn bitcoin
  "converts btc to sats"
  [btc]
  (* btc (Math/pow 10 8)))

(def addresses
  ["bcrt1qv5rv0g0py86zqmwcz05qf50qr34ckshawylvfq"])

(def category-names ["Category A" "Category B" "Category C"])

(def core-node1
  {::m.core-nodes/name        "bitcoin-alice"
   ::m.core-nodes/host        "bitcoin.bitcoin-alice"
   ::m.core-nodes/port        18443
   ::m.core-nodes/rpcuser     "rpcuser"
   ::m.core-nodes/rpcpass     "rpcpassword"
   ::m.core-nodes/wallet-name ""})

(def core-node2
  {::m.core-nodes/name        "bitcoin-bob"
   ::m.core-nodes/host        "bitcoin.bitcoin-bob"
   ::m.core-nodes/port        18443
   ::m.core-nodes/rpcuser     "rpcuser"
   ::m.core-nodes/rpcpass     "rpcpassword"
   ::m.core-nodes/wallet-name ""})

(def wallet-1
  {:label       "a"
   :blockheight 0
   :name        "Wallet A"
   :descriptor  "wpkh([7c6cf2c1/84h/1h/0h]tpubDDV8TbjuWeytsM7mAwTTkwVqWvmZ6TpMj1qQ8xNmNe6fZcZPwf1nDocKoYSF4vjM1XAoVdie8avWzE8hTpt8pgsCosTdAjnweSy7bR1kAwc/0/*)#8phlkw5l"
   :seed        ["universe" "loud" "stable" "patrol" "artwork" "chimney" "acoustic" "chief" "one"
                 "use" "object" "gossip" "enter" "green" "scout" "brother" "worry" "fancy" "olive"
                 "salmon" "chef" "repair" "hospital" "milk"]
   :node        "bitcoin-alice"
   :path        "m/84'/0'/0'"})

(def default-rates
  [{:rate 1813.
    :date (->inst "2021-10-09T11:06:37")}
   {:rate 1823.
    :date (->inst "2021-10-09T18:01:20")}
   {:rate 1812.
    :date (->inst "2021-10-10T07:41:47")}
   {:rate 1820.
    :date (->inst "2021-10-10T08:35:22")}
   {:rate 1811.
    :date (->inst "2021-10-10T14:30:14")}
   {:rate 1831.
    :date (->inst "2021-10-10T19:50:32")}
   {:rate 1747.
    :date (->inst "2021-10-11T11:29:31")}
   {:rate 1736.
    :date (->inst "2021-10-11T15:32:55")}
   {:rate 1624.
    :date (->inst "2021-10-16T07:15:11")}
   {:rate 1608.
    :date (->inst "2021-11-07T01:06:38")}
   {:rate 1608.
    :date (->inst "2021-11-07T12:38:45")}
   {:rate 1616.98454696378
    :date (->inst "2021-11-07T12:36:28")}
   {:rate 1601.2499997998439
    :date (->inst "2021-11-07T13:05:16")}
   {:rate 1602.3992403345683
    :date (->inst "2021-11-07T12:10:16")}
   {:rate 1608.2339002518654
    :date (->inst "2021-11-07T12:15:16")}
   {:rate 1595.
    :date (->inst "2021-11-07T16:00:00")}
   {:rate 1539.
    :date (->inst "2021-11-10T18:07:37")}])

(def sat-rates
  [{:rate 1.
    :date (->inst "2010-05-18T12:35:20")}])

(def lnd1-mnemonic
  ["abandon" "horror"  "because" "buddy"  "jump"  "satisfy"
   "escape"  "flee"    "tape"    "pull"   "bacon" "arm"
   "twenty"  "filter"  "burst"   "mirror" "ghost" "short"
   "work"    "home"    "punch"   "little"  "like" "gym"])

(def lnd2-mnemonic
  ["absorb"  "impulse"  "slide" "trumpet" "garage" "happy"
   "round"   "rely"     "rebel" "flower"  "vessel" "regular"
   "trick"   "mechanic" "bird"  "hope"    "appear" "oblige"
   "someone" "spell"    "robot" "riot"    "swamp"  "pulp"])

(def lnd1-key "02e21b44ba07591e43aa59a29f8631edb299d306d232a51a38f28d3892751dc13d")
(def lnd2-key "020e78000d4d907877ab352cd53c0dd382071c224b500c1fa05fb6f7902f5fa544")

(def lnd1-peers
  [{:ref        ["bob" "lnd-bob"]
    :flapCount  1
    :bytesSent  141
    :bytesRecv  141
    :syncType   "ACTIVE_SYNC"
    :satRecv    345
    :lastFlapNs 43
    :pingTime   7
    :inbound    false
    :satSent    1003}])

(def lnd2-peers
  [{:ref        ["alice" "lnd-alice"]
    :flapCount  2
    :bytesSent  141
    :bytesRecv  141
    :syncType   "ACTIVE_SYNC"
    :satRecv    3
    :lastFlapNs 5
    :pingTime   6
    :inbound    true
    :satSent    42069}])

(def lnd1-txes
  [{:description   "tx 1"
    ;; :account          "a"
    ;; :value            "1"
    :amount        1
    :blockHeight   1
    :blockHash     ""
    :txHash        ""
    :timeStamp     0
    :rawTxHex      ""
    :label         "TX1"
    :destAddresses [""]}
   {:description   "tx 2"
    ;; :account          "b"
    ;; :value            "2"
    :amount        2
    :blockHeight   2
    :blockHash     ""
    :txHash        ""
    :timeStamp     3
    :rawTxHex      ""
    :label         "TX2"
    :destAddresses [""]}])

(def lnd2-txes
  [{:description   "tx 3"
    ;; :account          "c"
    ;; :value            "1"
    :amount        3
    :blockHeight   3
    :blockHash     ""
    :txHash        ""
    :timeStamp     4
    :rawTxHex      ""
    :label         "TX3"
    :destAddresses [""]}])

(def lnd1
  {:name                "lnd-alice"
   :host                "lnd-alice.lnd-alice.svc.cluster.local"
   :port                "10009"
   :node                "bitcoin-alice"
   :mnemonic            lnd1-mnemonic
   :identityPubkey      lnd1-key
   :alias               "Node Alice"
   :blockHeight         7
   :syncedToChain       false
   :syncedToGraph       false
   :color               "#3399ff"
   :bestHeaderTimestamp ""
   :blockHash           ""
   :commitHash          ""
   :features            []
   :numActiveChannels   0
   :numInactiveChannels 0
   :numPeers            0
   :numPendingChannels  0
   :testnet             false
   :uris                []
   :version             ""
   :peers               lnd1-peers
   :txes                lnd1-txes})

(def lnd2
  {:name                "lnd-bob"
   :node                "bitcoin-bob"
   :host                "lnd-bob.lnd-bob.svc.cluster.local"
   :port                "10009"
   :mnemonic            lnd2-mnemonic
   :identityPubkey      lnd2-key
   :alias               "Node Bob"
   :blockHeight         8
   :syncedToChain       false
   :syncedToGraph       false
   :color               "#3399ff"
   :bestHeaderTimestamp ""
   :blockHash           ""
   :commitHash          ""
   :features            []
   :numActiveChannels   0
   :numInactiveChannels 0
   :numPeers            0
   :numPendingChannels  0
   :testnet             false
   :uris                []
   :version             ""
   :peers               lnd2-peers
   :txes                lnd2-txes})

(def default-currencies
  [{:name "Sats"
    :code "sats"}
   {:name "Dollars"
    :code "usd"}])

(def default-rate-sources
  [{:name       "identity"
    :url        "identity"
    :code       "sats"
    :isActive   false
    :isIdentity true
    :rates      sat-rates
    :path       "1"}
   {:name       "CoinLott0"
    :url        "https://www.coinlott0.localhost/api/v1/quotes/BTC-USD"
    :isActive   false
    :isIdentity false
    :path       ".rate"
    :code       "usd"}
   {:name       "Coinbase USD"
    :code       "usd"
    :url        "https://api.coinbase.com/v2/prices/spot?currency=USD"
    :isActive   true
    :isIdentity false
    :path       "100000000 / (.data.amount | tonumber)"}
   {:name       "BitPonzi"
    :url        "https://www.bitponzi.biz.localhost/cgi?id=3496709"
    :isActive   false
    :isIdentity false
    :path       ".rate"
    :code       "usd"}
   {:name       "DuckBitcoin"
    :url        "https://www.duckbitcoin.localhost/api/current-rates"
    :code       "usd"
    :isActive   false
    :isIdentity false
    :path       ".rate"
    :rates      default-rates}
   {:name       "Leviathan"
    :url        "https://www.leviathan.localhost/prices"
    :isActive   false
    :isIdentity false
    :path       ".rate"
    :code       "usd"}])

(def admin-categories
  [{:name "Admin Category A"}
   {:name "Admin Category B"}
   {:name "Admin Category C"}])

(def alice-categories
  [{:name "Category A"}
   {:name "Category B"}
   {:name "Category C"}])

(def bob-categories
  [{:name "Bob's Category A"}
   {:name "Bob's Category B"}
   {:name "Bob's Category C"}])

(def alice-usd-transactions
  [{:description "Payday"
    :date        (->inst "2021-10-01T00:00:00")
    :value       2000.}
   {:description "Transfer to exchange"
    :date        (->inst "2021-10-01T01:00:00")
    :value       -400.}])

(def alice-sat-transactions
  [{:description "Transfer to exchange"
    :date        (->inst "2021-10-01T01:00:00")
    :value       400.}])

(def bob-usd-transactions
  [{:description "Payday"
    :date        (->inst "2021-10-01T00:00:00")
    :value       2000.}
   {:description "Transfer to exchange"
    :date        (->inst "2021-10-01T01:00:00")
    :value       -600.}])

(def bob-sat-transactions
  [{:description "Transfer to exchange"
    :date        (->inst "2021-10-01T01:00:00")
    :value       600.}])

(def alice-accounts
  [{:name          "Bank Account"
    :initial-value 0
    :source        "DuckBitcoin"
    :transactions  alice-usd-transactions}
   {:name          "Exchange USD"
    :initial-value 0
    :source        "DuckBitcoin"
    :transactions  alice-sat-transactions}
   {:name          "Cash"
    :source        "DuckBitcoin"
    :initial-value 0}
   {:name          "Exchange Sats"
    :initial-value 0
    :source        "identity"}
   {:name          "LND1 On-chain"
    :initial-value 0
    :source        "identity"}
   ;; {:name          "hot wallet"
   ;;  :initial-value (bitcoin 1)
   ;;  :source        "identity"}
   ;; {:name          "duress account"
   ;;  :initial-value (bitcoin 0.0615)
   ;;  :source        "identity"}
   ;; {:name          "hodl stack"
   ;;  :initial-value (bitcoin 6.15)
   ;;  :source        "identity"}
   ])

(def bob-accounts
  [;; {:name          "Bank Account"
   ;;  :initial-value 0
   ;;  :source        "DuckBitcoin"
   ;;  :transactions  bob-usd-transactions}
   ;; {:name          "Exchange USD"
   ;;  :initial-value 0
   ;;  :source        "DuckBitcoin"
   ;;  :transactions  bob-sat-transactions}
   ;; {:name          "Cash"
   ;;  :source        "DuckBitcoin"
   ;;  :initial-value 0}
   ;; {:name          "Exchange Sats"
   ;;  :initial-value 0
   ;;  :source        "identity"}
   {:name          "LND2 On-chain"
    :initial-value 0
    :source        "identity"}
   ;; {:name          "hot wallet"
   ;;  :initial-value (bitcoin 1)
   ;;  :source        "identity"}
   ;; {:name          "duress account"
   ;;  :initial-value (bitcoin 0.0615)
   ;;  :source        "identity"}
   ;; {:name          "hodl stack"
   ;;  :initial-value (bitcoin 6.15)
   ;;  :source        "identity"}
   ])

(def admin-data
  {:username   "admin"
   :password   m.users/default-password
   :accounts   []
   :wallets    [wallet-1]
   :categories admin-categories})

(def alice-data
  {:username   "alice"
   :password   m.users/default-password
   :accounts   alice-accounts
   :categories alice-categories
   :ln-nodes   [lnd1]
   :wallets    []})

(def bob-data
  {:username   "bob"
   :password   m.users/default-password
   :accounts   bob-accounts
   :categories bob-categories
   :ln-nodes   [lnd2]})

(def carol-data
  {:username "carol"
   :password m.users/default-password
   :categories
   [{:name "Category A"}
    {:name "Category B"}
    {:name "Category C"}]})

(def dave-data
  {:username "dave"
   :password m.users/default-password
   :categories
   [{:name "Category A"}
    {:name "Category B"}
    {:name "Category C"}]})

(def eve-data
  {:username "eve"
   :password m.users/default-password
   :categories
   [{:name "Category A"}
    {:name "Category B"}
    {:name "Category C"}]})

(def users
  [admin-data
   alice-data
   bob-data])

(def core-node-data [core-node1 core-node2])

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
    (xt/submit-tx node txes)))

(>defn seed-rate!
  [_user-id currency-id source-id {:keys [date rate]}]
  [::m.users/id ::m.currencies/id ::m.rate-sources/id any? => any?]
  (a.rates/add-rate
   source-id
   {::m.rates/currency currency-id
    ::m.rates/date     date
    ::m.rates/rate     rate}))

(defn seed-peer!
  [node-id {:keys [ref] :as peer}]
  (let [[target-username target-node] ref]
    (if-let [target-user-id (q.users/find-eid-by-name target-username)]
      (if-let [target-id (q.ln-nodes/find-id-by-user-and-name target-user-id target-node)]
        (let [{:keys [host identity-pubkey]} (q.ln-nodes/read-record target-id)
              params                         (-> peer
                                                 (assoc ::m.ln-peers/address host)
                                                 (assoc ::m.ln-peers/pubkey identity-pubkey)
                                                 (set/rename-keys m.ln-peers/rename-map))]
          (q.ln-peers/add-peer! node-id (log/spy :info params)))
        (throw (RuntimeException. (str "no ref: " ref))))
      (throw (RuntimeException. "no user")))))

(defn seed-currencies!
  []
  (log/info "Seeding Currencies")
  (doseq [{:keys [code name]} default-currencies]
    (let [currency {::m.currencies/code code
                    ::m.currencies/name name}]
      (q.currencies/create-record (log/spy :info currency)))))

(defn seed-rate-sources!
  []
  (log/info "Seeding RateSources")
  (doseq [{:keys [isActive isIdentity code name path url]} default-rate-sources]
    (when-let [currency-id (q.currencies/find-eid-by-code code)]
      (let [rate-source {::m.rate-sources/name      name
                         ::m.rate-sources/currency  currency-id
                         ::m.rate-sources/url       url
                         ::m.rate-sources/active?   isActive
                         ::m.rate-sources/identity? isIdentity
                         ::m.rate-sources/path      path}]
        (q.rate-sources/create-record (log/spy :info rate-source))))))

(defn seed-rates!
  []
  (log/info "Seeding Rates")
  (doseq [{:keys [name code rates]} default-rate-sources]
    (if-let [currency-id (q.currencies/find-eid-by-code code)]
      (if-let [source-id (q.rate-sources/find-id-by-currency-and-name currency-id name)]
        (doseq [{:keys [date rate]} rates]
          (let [rate {::m.rates/currency currency-id
                      ::m.rates/date     date
                      ::m.rates/source   source-id
                      ::m.rates/rate     rate}]
            (q.rates/create-record (log/spy :info rate))))
        (throw (RuntimeException. "Failed to find source")))
      (throw (RuntimeException. "Failed to find currency")))))

(defn seed-users!
  [users]
  (log/info "Seeding Users")
  (doseq [{:keys [username password]} users]
    (a.authentication/do-register username password)))

(defn seed-categories!
  [users]
  (doseq [{:keys [categories username]} users]
    (let [user-id (q.users/find-eid-by-name username)]
      (doseq [{:keys [name]} categories]
        (q.categories/create-record {::m.categories/name name
                                     ::m.categories/user user-id})))))

(defn seed-ln-nodes!
  [users]
  (log/info "seed ln-nodes")
  (doseq [{:keys [username ln-nodes]} users]
    (let [user-id (q.users/find-eid-by-name username)]
      (doseq [{:keys     [name host port mnemonic] :as info
               node-name :node} ln-nodes]
        (if-let [core-id (q.core-nodes/find-id-by-name node-name)]
          (let [ln-node {::m.ln-nodes/name      name
                         ::m.ln-nodes/core-node core-id
                         ::m.ln-nodes/host      host
                         ::m.ln-nodes/port      port
                         ::m.ln-nodes/user      user-id
                         ::m.ln-nodes/mnemonic  mnemonic}
                node-id (q.ln-nodes/create-record (log/spy :info ln-node))
                info    (set/rename-keys info m.ln-info/rename-map)]
            (a.ln-nodes/save-info! node-id (log/spy :info info)))
          (throw (RuntimeException. (str "Failed to find node: " node-name))))))))

(defn seed-ln-peers!
  [users]
  (log/info "Seeding LNPeers")
  (doseq [{:keys [username ln-nodes]} users]
    (let [user-id (q.users/find-eid-by-name username)]
      (doseq [{:keys [name peers]} ln-nodes]
        (if-let [node-id (q.ln-nodes/find-id-by-user-and-name user-id name)]
          (doseq [{[target-username target-node] :ref :as peer} peers]
            (if-let [target-user-id (q.users/find-eid-by-name target-username)]
              (if-let [target-id (q.ln-nodes/find-id-by-user-and-name target-user-id target-node)]
                (let [{:keys [host identity-pubkey]} (q.ln-nodes/read-record target-id)
                      peer                           (-> peer
                                                         (assoc ::m.ln-peers/address host)
                                                         (assoc ::m.ln-peers/pubkey identity-pubkey)
                                                         (set/rename-keys m.ln-peers/rename-map))]
                  (q.ln-peers/add-peer! node-id peer))
                (throw (RuntimeException. (str "no target: " target-node))))
              (throw (RuntimeException. "no user"))))
          (throw (RuntimeException. "no node")))))))

(defn seed-ln-txes!
  [users]
  (log/info "Seeding LNTransactions")
  (doseq [{:keys [ln-nodes username]} users]
    (if-let [user-id (q.users/find-eid-by-name username)]
      (doseq [{:keys [name txes]} ln-nodes]
        (if-let [ln-node-id (q.ln-nodes/find-id-by-user-and-name user-id name)]
          (if-let [ln-node (log/spy :info (q.ln-nodes/read-record ln-node-id))]
            (if-let [core-node-id (::m.ln-nodes/core-node ln-node)]
              (doseq [tx txes]
                (let [tx           (set/rename-keys tx m.ln-tx/rename-map)
                      tx           (assoc tx ::m.ln-tx/node ln-node-id)
                      tx-hash      (::m.ln-tx/tx-hash tx)
                      block-hash   (::m.ln-tx/block-hash tx)
                      block-height (::m.ln-tx/block-height tx)
                      core-tx-id   (a.core-tx/register-tx core-node-id block-hash block-height tx-hash)
                      tx           (assoc tx ::m.ln-tx/core-tx core-tx-id)]
                  (q.ln-tx/add-tx ln-node-id (log/spy :info tx))))
              (throw (RuntimeException. "Node does not contain a core node id")))
            (throw (RuntimeException. "Failed to read ln node")))
          (throw (RuntimeException. "Failed to find ln node"))))
      (throw (RuntimeException. "Failed to find ln tx")))))

(defn seed-accounts!
  [users]
  (log/info "Seeding Accounts")
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
  (log/info "Seeding Transactions")
  (doseq [{:keys [accounts username]} users]
    (if-let [user-id (q.users/find-eid-by-name username)]
      (doseq [{:keys [name transactions]} accounts]
        (if-let [account-id (q.accounts/find-id-by-user-and-name user-id name)]
          (doseq [{:keys [description date value]} transactions]
            (let [transaction {::m.transactions/date        date
                               ::m.transactions/description description
                               ::m.transactions/account     account-id
                               ::m.transactions/value       value}]
              (q.transactions/create-record (log/spy :info transaction))))
          (throw (RuntimeException. "Failed fo find account"))))
      (throw (RuntimeException. "Failed to find user")))))

(defn item-report
  []
  (log/infof "Users: %s" (count (q.users/index-ids)))
  (log/infof "Categories: %s" (count (q.categories/index-ids)))
  (log/infof "Currencies: %s" (count (q.currencies/index-ids)))
  (log/infof "Rate Sources: %s" (count (q.rate-sources/index-ids)))
  (log/infof "Rates: %s" (count (q.rates/index-ids)))
  (log/infof "Accounts: %s" (count (q.accounts/index-ids)))
  (log/infof "Transactions: %s" (count (q.transactions/index-ids)))
  (log/infof "Ln Nodes: %s" (count (q.ln-nodes/index-ids)))
  (log/infof "Ln Peers: %s" (count (q.ln-peers/index-ids)))
  (log/infof "Ln txes: %s" (count (q.ln-tx/index-ids))))

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
    (if-let [node-id (q.core-nodes/find-id-by-name node-name)]
      (let [tx    (assoc tx ::m.core-tx/node node-id)
            tx-id (q.core-tx/create-record tx)]
        (doseq [tx-in in]
          (let [tx-in (assoc tx-in ::m.core-tx-in/transaction tx-id)]
            (q.core-tx-in/create-record (log/spy :info tx-in)))))
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
              node-id           (q.core-nodes/find-id-by-name node-name)
              wallet-id         (q.wallets/create-record
                                 {::m.wallets/name       name
                                  ::m.wallets/derivation path
                                  ::m.wallets/node       node-id
                                  ::m.wallets/user       user-id})]
          (doseq [[i word] (map-indexed vector seed)]
            (let [props {::m.words/wallet   wallet-id
                         ::m.words/word     word
                         ::m.words/position (inc i)}]
              (q.words/create-record props))))
        wallet))))

(defn seed-addresses!
  [addresses]
  (doseq [address addresses]
    (q.core-address/create-record {::m.core-address/address address})))

(defn seed-db!
  []
  (create-navlinks!)
  (dt/set-timezone! default-timezone)

  (doseq [data core-node-data]
    (q.core-nodes/create-record data))
  #_(seed-core-txes!)

  (seed-currencies!)
  (seed-rate-sources!)
  (seed-rates!)
  (seed-users! users)
  (seed-categories! users)
  (seed-accounts! users)
  (seed-transactions! users)
  (seed-ln-nodes! users)
  (seed-wallets! users)
  (seed-addresses! [])
  ;; (seed-ln-peers! users)
  ;; (seed-ln-txes! users)

  (log/info "Done seeding")
  (item-report))

(comment

  (seed-db!)

  nil)
