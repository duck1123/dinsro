(ns dinsro.seed
  (:require
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.users :as m.users]
   [dinsro.specs :refer [->inst]]
   [reitit.coercion.spec]))

(def default-password m.users/default-password)
(def default-timezone "America/Detroit")

(defn bitcoin
  "converts btc to sats"
  [btc]
  (* btc (Math/pow 10 8)))

(def addresses
  ["bcrt1qv5rv0g0py86zqmwcz05qf50qr34ckshawylvfq"])

(def category-names ["Category A" "Category B" "Category C"])

(def core-node1
  {::m.c.nodes/name    "bitcoin-alice"
   ::m.c.nodes/host    "bitcoin.alice"
   ::m.c.nodes/port    18443
   ::m.c.nodes/rpcuser "rpcuser"
   ::m.c.nodes/rpcpass "rpcpassword"
   :chain              "bitcoin"
   :network            "regtest"
   :peers              ["bitcoin-bob"]})

(def core-node2
  {::m.c.nodes/name    "bitcoin-bob"
   ::m.c.nodes/host    "bitcoin.bob"
   ::m.c.nodes/port    18443
   :chain              "bitcoin"
   :network            "regtest"
   ::m.c.nodes/rpcuser "rpcuser"
   ::m.c.nodes/rpcpass "rpcpassword"})

(def core-node3
  {::m.c.nodes/name    "bitcoin-larry"
   ::m.c.nodes/host    "bitcoin.larry"
   ::m.c.nodes/port    18332
   :chain              "bitcoin"
   :network            "testnet"
   ::m.c.nodes/rpcuser "rpcuser"
   ::m.c.nodes/rpcpass "rpcpassword"})

;; universe loud stable patrol artwork chimney acoustic chief one use object gossip enter green scout brother worry fancy olive salmon chef repair hospital milk

(def wallet-1
  {:label       "a"
   :blockheight 0
   :name        "Wallet A"
   :descriptor  "wpkh([7c6cf2c1/84h/1h/0h]tpubDDV8TbjuWeytsM7mAwTTkwVqWvmZ6TpMj1qQ8xNmNe6fZcZPwf1nDocKoYSF4vjM1XAoVdie8avWzE8hTpt8pgsCosTdAjnweSy7bR1kAwc/0/*)#8phlkw5l"
   :seed        ["universe" "loud"   "stable" "patrol"  "artwork"  "chimney"
                 "acoustic" "chief"  "one"    "use"     "object"   "gossip"
                 "enter"    "green"  "scout"  "brother" "worry"    "fancy"
                 "olive"    "salmon" "chef"   "repair"  "hospital" "milk"]
   :node        "bitcoin-alice"
   :path        "m/84'/0'/0'"})

(def wallet-2
  {:label        "a"
   :blockheight  0
   :name         "Wallet B"
   :descriptor   ""
   :bip39seed    "88eb1087c7e97a9c7f1895e4e42a2e64bdc51b5364c8c80e3ab7041b9267ae747b3a108c85cd57f23c0788b200bd9844c80c62888f5affb1e3939d5e9750d145"
   :bip32rootkey "tprv8ZgxMBicQKsPehNiYwinuVTeh9GzDmr8ToRZ6z4jYXynkwJi9bQPfhGwJ2q7GBKkpuQcWwktyzqYm33NKc6cMCRYJXn6BakdYbT9TLvnJx8"
   :seed         ["violin" "bleak"  "raw"  "mistake" "toddler" "wire"
                  "kind"   "state"  "aim"  "game"    "glass"   "peace"
                  "bone"   "luxury" "list" "flash"   "music"   "impulse"
                  "naive"  "type"   "wet"  "reform"  "panic"   "expand"]
   :node         "bitcoin-alice"
   :path         "m/84'/0'/0'"})

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
    :date (->inst "2021-11-10T18:07:37")}
   {:rate 4647.
    :date (->inst "2022-07-09T09:50:00")}
   {:rate 4705.
    :date (->inst "2022-08-20T09:13:00")}
   {:rate 5051.
    :date (->inst "2022-09-03T18:50:00")}
   {:rate 5027.
    :date (->inst "2022-09-05T11:06:00")}])

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
    :amount        1
    :blockHeight   1
    :blockHash     ""
    :txHash        ""
    :timeStamp     0
    :rawTxHex      ""
    :label         "TX1"
    :destAddresses [""]}
   {:description   "tx 2"
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
    :amount        3
    :blockHeight   3
    :blockHash     ""
    :txHash        ""
    :timeStamp     4
    :rawTxHex      ""
    :label         "TX3"
    :destAddresses [""]}])

(def lnd1
  {:name            "lnd-alice"
   :host            "lnd.alice.svc.cluster.local"
   :fileserver-host "fileserver.alice"
   :port            "10009"
   :node            "bitcoin-alice"
   :mnemonic        lnd1-mnemonic
   :remote-nodes
   [{:host   "lnd.bob.svc.cluster.local"
     :pubKey lnd2-key}]
   :peers           []
   :txes            []})

(def lnd2
  {:name            "lnd-bob"
   :node            "bitcoin-bob"
   :host            "lnd.bob.svc.cluster.local"
   :fileserver-host "fileserver.bob"
   :port            "10009"
   :mnemonic        lnd2-mnemonic
   :remote-nodes    [{:host   "lnd.alice.svc.cluster.local"
                      :pubKey lnd1-key}]
   :peers           []
   :txes            []})

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
    :code       "usd"
    :rates      []}
   {:name       "BitPonzi"
    :url        "https://www.bitponzi.biz.localhost/cgi?id=3496709"
    :isActive   false
    :isIdentity false
    :path       ".rate"
    :code       "usd"
    :rates      []}
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
    :code       "usd"
    :rates      []}])

(def admin-categories
  [{:name "Admin Category A"}
   {:name "Admin Category B"}
   {:name "Admin Category C"}])

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

(def bob-sat-transactions
  [{:description "Transfer to exchange"
    :date        (->inst "2021-10-01T01:00:00")
    :value       600.}])

(def bank-account "Alice's Bank Account")
(def exchange-usd "Exchange USD")
(def exchange-sats "Exchange sats")
(def wallet-a-account "Wallet A")
(def bob-bank "Bank Account")
(def bob-exchange "Bob's Exchange")

(def admin-data
  {:username     "admin"
   :password     m.users/default-password
   :role         :account.role/admin
   :accounts     []
   :transactions []
   :ln-nodes     []
   :wallets      []
   :categories   admin-categories})

(def alice-data
  {:username     "alice"
   :password     m.users/default-password
   :role         :account.role/user
   :accounts     [{:name bank-account :initial-value 0 :source "DuckBitcoin"}
                  {:name exchange-usd :initial-value 0 :source "DuckBitcoin"}
                  {:name "Cash" :source "DuckBitcoin" :initial-value 0}
                  {:name exchange-sats :initial-value 0 :source "identity"}
                  {:name wallet-a-account :initial-value 0 :source "identity" :wallet "Wallet A"}
                  {:name "LND1 On-chain" :initial-value 0 :source "identity"}]
   :categories   [{:name "Category A"}
                  {:name "Category B"}
                  {:name "Category C"}]
   :transactions [{:date        (->inst "2022-09-03T16:30:00")
                   :description "Initial deposit"
                   :debits      [{:value 2000. :account bank-account}]}
                  {:date        (->inst "2022-09-03T16:32:00")
                   :description "Transfer to exchange"
                   :debits      [{:value -100. :account bank-account}
                                 {:value 100. :account exchange-usd}]}
                  {:date        (->inst "2022-09-03T16:35:00")
                   :description "Buy Bitcoin"
                   :debits      [{:value -50. :account exchange-usd}
                                 {:value 5000000000. :account exchange-sats}]}
                  {:date        (->inst "2022-09-03T16:38:00")
                   :description "withdraw sats"
                   :debits      [{:value -5000000000. :account exchange-sats}
                                 {:value 5000000000. :account wallet-a-account}]}]
   :ln-nodes     [lnd1]
   :wallets      [wallet-1]})

(def bob-data
  {:username     "bob"
   :password     m.users/default-password
   :role         :account.role/user
   :accounts     [{:name bob-bank :initial-value 0 :source "DuckBitcoin"}
                  {:name bob-exchange :initial-value 0 :source "DuckBitcoin"}
                  {:name "LND2 On-chain" :initial-value 0 :source "identity"}]
   :transactions [{:date        (->inst "2022-09-05T10:57:00")
                   :description "Payday"
                   :debits      [{:value 2000. :account bob-bank}]}
                  {:date        (->inst "2022-09-05T10:59:00")
                   :description "Transfer to exchange"
                   :debits      [{:value -600. :account bob-bank}
                                 {:value 600. :account bob-exchange}]}]
   :categories   bob-categories
   :ln-nodes     [lnd2]
   :wallets      [wallet-2]})

(def carol-data
  {:username     "carol"
   :password     m.users/default-password
   :role         :account.role/user
   :transactions []
   :categories
   [{:name "Category A"}
    {:name "Category B"}
    {:name "Category C"}]})

(def dave-data
  {:username     "dave"
   :password     m.users/default-password
   :role         :account.role/user
   :transactions []
   :categories
   [{:name "Category A"}
    {:name "Category B"}
    {:name "Category C"}]})

(def eve-data
  {:username     "eve"
   :password     m.users/default-password
   :transactions []
   :categories
   [{:name "Category A"}
    {:name "Category B"}
    {:name "Category C"}]})

(def users
  [admin-data
   alice-data
   bob-data])

(def core-node-data [core-node1 core-node2])

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
