(ns dinsro.seed
  (:require
   ;; [dinsro.components.seed :as c.seed]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.users :as m.users]
   [reitit.coercion.spec]
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
  {::m.c.nodes/name        "bitcoin-alice"
   ::m.c.nodes/host        "bitcoin.alice"
   ::m.c.nodes/port        18443
   ::m.c.nodes/rpcuser     "rpcuser"
   ::m.c.nodes/rpcpass     "rpcpassword"})

(def core-node2
  {::m.c.nodes/name        "bitcoin-bob"
   ::m.c.nodes/host        "bitcoin.bob"
   ::m.c.nodes/port        18443
   ::m.c.nodes/rpcuser     "rpcuser"
   ::m.c.nodes/rpcpass     "rpcpassword"})

(def core-node3
  {::m.c.nodes/name    "bitcoin-larry"
   ::m.c.nodes/host    "bitcoin.larry"
   ::m.c.nodes/port    18332
   ::m.c.nodes/rpcuser "rpcuser"
   ::m.c.nodes/rpcpass "rpcpassword"})

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
   :host                "lnd.alice.svc.cluster.local"
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
   :host                "lnd.bob.svc.cluster.local"
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
   #_{:name       "Coinbase USD"
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

(def core-node-data [core-node1 core-node2 core-node3])

(def default-chains ["bitcoin" "fakecoin"])

(def default-networks
  {"bitcoin" ["mainnet" "testnet" "regtest"]
   "fakecoin" ["mainnet"]})

(defn get-seed-data
  []
  {:default-chains       default-chains
   :default-currencies   default-currencies
   :default-networks     default-networks
   :default-rate-sources default-rate-sources
   :default-timezone     default-timezone
   :core-node-data       core-node-data
   :users                users})

(comment

  nil)
