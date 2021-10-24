(ns dinsro.seed
  (:require
   [clojure.set :as set]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [com.fulcrologic.rad.type-support.date-time :as dt]
   [crux.api :as crux]
   [dinsro.actions.authentication :as a.authentication]
   [dinsro.actions.ln-nodes :as a.ln-nodes]
   [dinsro.actions.rates :as a.rates]
   [dinsro.components.crux :as c.crux]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.core-nodes :as m.core-nodes]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.ln-info :as m.ln-info]
   [dinsro.model.ln-nodes :as m.ln-nodes]
   [dinsro.model.ln-peers :as m.ln-peers]
   [dinsro.model.ln-transactions :as m.ln-tx]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.rates :as m.rates]
   [dinsro.model.seed :as seed]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   [dinsro.queries.accounts :as q.accounts]
   [dinsro.queries.categories :as q.categories]
   [dinsro.queries.core-nodes :as q.core-nodes]
   [dinsro.queries.currencies :as q.currencies]
   [dinsro.queries.ln-nodes :as q.ln-nodes]
   [dinsro.queries.ln-peers :as q.ln-peers]
   [dinsro.queries.ln-transactions :as q.ln-tx]
   [dinsro.queries.rate-sources :as q.rate-sources]
   [dinsro.queries.rates :as q.rates]
   [dinsro.queries.transactions :as q.transactions]
   [dinsro.queries.users :as q.users]
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

(def links
  [["accounts"        "Accounts"        "/accounts"        :dinsro.ui.index-accounts/IndexAccountsPage]
   ["admin"           "Admin"           "/admin"           :dinsro.ui.admin/AdminPage]
   ["categories"      "Categories"      "/categories"      :dinsro.ui.index-categories/IndexCategoriesPage]
   ["channels"        "Channels"        "/ln-channels"     :dinsro.ui.ln-channels/LNChannelsReport]
   ["core-nodes"      "Core Nodes"      "/core-nodes"      :dinsro.ui.core-nodes/CoreNodesReport]
   ["currencies"      "Currencies"      "/currencies"      :dinsro.ui.currencies/CurrenciesReport]
   ["home"            "Home"            "/"                :dinsro.ui.home/HomePage2]
   ["lightning-nodes" "Lightning Nodes" "/ln-nodes"        :dinsro.ui.ln-nodes/LightningNodesReport]
   ["login"           "Login"           "/login"           :dinsro.ui.login/LoginPage]
   ["peers"           "Peers"           "/ln-peers"        :dinsro.ui.ln-peers/LNPeersReport]
   ["rates"           "Rates"           "/rates"           :dinsro.ui.index-rates/IndexRatesPage]
   ["rate-sources"    "Rate Sources"    "/rate-sources"    :dinsro.ui.index-rate-sources/IndexRateSourcesPage]
   ["registration"    "Registration"    "/register"        :dinsro.ui.registration/RegistrationPage]
   ["settings"        "Settings"        "/settings"        :dinsro.ui.settings/SettingsPage]
   ["transactions"    "Transactions"    "/transactions"    :dinsro.ui.index-transactions/IndexTransactionsPage]
   ["tx"              "LN TXes"         "/ln-transactions" :dinsro.ui.ln-transactions/LNTransactionsReport]
   ["users"           "User"            "/users"           :dinsro.ui.index-users/IndexUsersPage]])

(def category-names ["Category A" "Category B" "Category C"])

(def core-node1
  {::m.core-nodes/name    "main"
   ::m.core-nodes/host    "bitcoind.bitcoin"
   ::m.core-nodes/port    "18443"
   ::m.core-nodes/rpcuser "rpcuser"
   ::m.core-nodes/rpcpass "rpcpassword"})

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
    :date (->inst "2021-10-16T07:15:11")}])

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
  [{:ref        ["bob" "lnd2"]
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
  [{:ref ["alice" "lnd1"]
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
  [{:description      "tx 1"
    ;; :account          "a"
    ;; :value            "1"
    :numConfirmations 0
    :amount           1
    :blockHeight      1
    :blockHash        ""
    :txHash           ""
    :timeStamp        0
    :rawTxHex        ""
    :label            "TX1"
    :destAddresses    [""]}
   {:description      "tx 2"
    ;; :account          "b"
    ;; :value            "2"
    :numConfirmations 0
    :amount           2
    :blockHeight      2
    :blockHash        ""
    :txHash           ""
    :timeStamp        3
    :rawTxHex        ""
    :label            "TX2"
    :destAddresses    [""]}])

(def lnd2-txes
  [{:description      "tx 3"
    ;; :account          "c"
    ;; :value            "1"
    :numConfirmations 3
    :amount           3
    :blockHeight      3
    :blockHash        ""
    :txHash           ""
    :timeStamp        4
    :rawTxHex        ""
    :label            "TX3"
    :destAddresses    [""]}])

(def lnd1
  {:name                "lnd1"
   :host                "lnd1-internal.lnd1.svc.cluster.local"
   :port                "10009"
   :mnemonic            lnd1-mnemonic
   :identityPubkey      lnd1-key
   :alias               "Node One"
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
  {:name                "lnd2"
   :host                "lnd2-internal.lnd2.svc.cluster.local"
   :port                "10009"
   :mnemonic            lnd2-mnemonic
   :identityPubkey      lnd2-key
   :alias               "Node Two"
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
  [{:name "identity"
    :url  "identity"
    :code "sats"}
   {:name "CoinLott0"
    :url  "https://www.coinlott0.localhost/api/v1/quotes/BTC-USD"
    :code "usd"}
   {:name "BitPonzi"
    :url  "https://www.bitponzi.biz.localhost/cgi?id=3496709"
    :code "usd"}
   {:name "DuckBitcoin"
    :url  "https://www.duckbitcoin.localhost/api/current-rates"
    :code "usd"
    :rates    default-rates}
   {:name "Leviathan"
    :url  "https://www.leviathan.localhost/prices"
    :code "usd"}])

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
    :transactions alice-usd-transactions}
   {:name          "Exchange USD"
    :initial-value 0
    :source        "DuckBitcoin"
    :transactions alice-sat-transactions}
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

(def admin-data
  {:username   "admin"
   :password   m.users/default-password
   :accounts   []
   :categories admin-categories})

(def alice-data
  {:username   "alice"
   :password   m.users/default-password
   :accounts   alice-accounts
   :categories alice-categories
   :ln-nodes   [lnd1]})

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

(def core-node-data [core-node1])

(defn create-navlinks!
  []
  (let [node (:main c.crux/crux-nodes)
        add  (fnil conj [])
        data (reduce
              (fn [data link]
                (let [[id name href target] link]
                  (update data :navlinks add (seed/new-navlink id name href target))))
              {} links)
        txes (->> data
                  vals
                  flatten
                  (mapv #(vector :crux.tx/put %)))]
    (crux/submit-tx node txes)))

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
  (doseq [{:keys [code name url]} default-rate-sources]
    (when-let [currency-id (q.currencies/find-eid-by-code code)]
      (let [rate-source {::m.rate-sources/name     name
                         ::m.rate-sources/currency currency-id
                         ::m.rate-sources/url      url}]
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
      (doseq [{:keys [name host port mnemonic] :as info} ln-nodes]
        (let [node    {::m.ln-nodes/name     name
                       ::m.ln-nodes/host     host
                       ::m.ln-nodes/port     port
                       ::m.ln-nodes/user     user-id
                       ::m.ln-nodes/mnemonic mnemonic}
              node-id (q.ln-nodes/create-record (log/spy :info node))
              info    (set/rename-keys info m.ln-info/rename-map)]
          (a.ln-nodes/save-info! node-id (log/spy :info info)))))))

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
        (if-let [node-id (q.ln-nodes/find-id-by-user-and-name user-id name)]
          (doseq [tx txes]
            (let [tx (assoc (set/rename-keys tx m.ln-tx/rename-map) ::m.ln-tx/node node-id)]
              (q.ln-tx/add-tx node-id (log/spy :info tx))))
          (throw (RuntimeException. "Failed to find node"))))
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
             {::m.accounts/name name
              ::m.accounts/currency currency-id
              ::m.accounts/user user-id
              ::m.accounts/initial-value initial-value}))
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

(defn seed-db!
  []
  (create-navlinks!)
  (dt/set-timezone! default-timezone)

  (doseq [data core-node-data]
    (q.core-nodes/create-record data))

  (seed-currencies!)
  (seed-rate-sources!)
  (seed-rates!)

  (seed-users! users)
  (seed-categories! users)
  (seed-accounts! users)
  (seed-transactions! users)
  (seed-ln-nodes! users)
  (seed-ln-peers! users)
  (seed-ln-txes! users)
  (log/info "Done seeding")
  (item-report))
