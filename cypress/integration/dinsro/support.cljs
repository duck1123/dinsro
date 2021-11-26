(ns dinsro.support
  (:require
   [cognitect.transit :as t]))

(def cy js/cy)

(def DEFAULT-USERNAME "admin")
(def DEFAULT-PASSWORD "hunter2")

(defn login
  ([]
   (login DEFAULT-USERNAME DEFAULT-PASSWORD))
  ([username password]
   (.. cy (log "logging in"))
   (.. cy (get ":nth-child(1) > .control > div > .input") clear (type username))
   (.. cy (get ":nth-child(2) > .control > div > .input") clear (type password))
   (.. cy (get ".control > .ui") click)))

(def currencies
  [{:dinsro.model.curencies/id   "1"
    :dinsro.model.curencies/name "USD"
    :dinsro.model.curencies/code "usd"}])

(def users
  [{:dinsro.model.users/name "admin"
    :dinsro.model.users/id   "admin"}
   {:dinsro.model.users/name "admin2"
    :dinsro.model.users/id   "admin2"}])

(def dropdown-links
  [{:dinsro.model.navlink/id     :users,
    :dinsro.model.navlink/name   "Users",
    :dinsro.model.navlink/target :dinsro.ui.users/UsersReport}
   {:dinsro.model.navlink/id   :currencies,
    :dinsro.model.navlink/name "Currencies",
    :dinsro.model.navlink/target
    :dinsro.ui.currencies/CurrenciesReport}
   {:dinsro.model.navlink/id   :categories,
    :dinsro.model.navlink/name "Categories",
    :dinsro.model.navlink/target
    :dinsro.ui.categories/CategoriesReport}
   {:dinsro.model.navlink/id     :rates,
    :dinsro.model.navlink/name   "Rates",
    :dinsro.model.navlink/target :dinsro.ui.rates/RatesReport}
   {:dinsro.model.navlink/id   :rate-sources,
    :dinsro.model.navlink/name "Rate Sources",
    :dinsro.model.navlink/target
    :dinsro.ui.rate-sources/RateSourcesReport}
   {:dinsro.model.navlink/id   :tx,
    :dinsro.model.navlink/name "LN TXes",
    :dinsro.model.navlink/target
    :dinsro.ui.ln-transactions/LNTransactionsReport}
   {:dinsro.model.navlink/id     :peers,
    :dinsro.model.navlink/name   "Peers",
    :dinsro.model.navlink/target :dinsro.ui.ln-peers/LNPeersReport}
   {:dinsro.model.navlink/id   :lightning-nodes,
    :dinsro.model.navlink/name "Lightning Nodes",
    :dinsro.model.navlink/target
    :dinsro.ui.ln-nodes/LightningNodesReport}
   {:dinsro.model.navlink/id     :core-nodes,
    :dinsro.model.navlink/name   "Core Nodes",
    :dinsro.model.navlink/target :dinsro.ui.core-nodes/CoreNodesReport}
   {:dinsro.model.navlink/id     :core-blocks,
    :dinsro.model.navlink/name   "Core Blocks",
    :dinsro.model.navlink/target :dinsro.ui.core-block/CoreBlockReport}
   {:dinsro.model.navlink/id     :core-txes,
    :dinsro.model.navlink/name   "Core Transactions",
    :dinsro.model.navlink/target :dinsro.ui.core-tx/CoreTxReport}
   {:dinsro.model.navlink/id   :transactions,
    :dinsro.model.navlink/name "Transactions",
    :dinsro.model.navlink/target
    :dinsro.ui.transactions/TransactionsReport}
   {:dinsro.model.navlink/id     :accounts,
    :dinsro.model.navlink/name   "Accounts",
    :dinsro.model.navlink/target :dinsro.ui.accounts/AccountsReport}
   {:dinsro.model.navlink/id     :admin,
    :dinsro.model.navlink/name   "Admin",
    :dinsro.model.navlink/target :dinsro.ui.admin/AdminPage}]
  )

(defn handle-keyword
  ([k] (handle-keyword k nil))
  ([k v]
   (println "keyword: " k v)
   (condp = k
     :com.wsscode.pathom.core/errors
     {}

     :dinsro.model.currencies/all-currencies
     {:dinsro.model.curencies/all-currencies
      currencies}

     :dinsro.model.rate-sources/all-rate-sources
     {:dinsro.model.rate-sources/all-rate-sources []}

     :dinsro.model.users/all-users
     {:dinsro.model.users/all-users users}

     '(dinsro.mutations.session/check-session)
     {`dinsro.mutations.session/check-session
      {:com.fulcrologic.rad.authorization/provider :local
       :com.fulcrologic.rad.authorization/status   :success
       :session/current-user-ref                   nil
       :time-zone/zone-id                          nil}}

     :dinsro.model.settings/site-config
     {:dinsro.model.settings/site-config
      {:dinsro.model.settings/id           :main
       :dinsro.model.settings/initialized? true
       :dinsro.model.settings/loaded?      true
       :dinsro.model.settings/auth
       {:com.fulcrologic.rad.authorization/provider :local}}}

     :dinsro.model.navlink/current-navbar
     {:dinsro.model.navlink/current-navbar
      {:navbar/id                           :main
       :dinsro.model.navlink/dropdown-links dropdown-links
       [:com.fulcrologic.rad.authorization/authorization :local]
       {:com.fulcrologic.rad.authorization/authorization :local}}}

     (do
       (js/console.error (str "unknown keyword: " k))
       (println k)
       {}))))

(defn dispatch-item
  [p]
  ;; (js/console.log "parsed" p)
  (condp = (type p)
    PersistentArrayMap
    (let [ms2 (map (fn [[k v]] (handle-keyword k v)) p)]
      ;; (println "ms2" ms2)
      (apply merge ms2))

    List
    (apply merge (map dispatch-item p))

    Keyword
    (handle-keyword p)

    (do
      (js/console.error "unknown type" p)
      {})))

(defn dispatch-pathom
  [req]
  (let [body   (.-body req)
        r      (t/reader :json)
        parsed (t/read r (js/JSON.stringify body))
        ms     (map dispatch-item parsed)]
    ;; (println "ms" ms)
    (apply merge ms)))

(defn handle-pathom
  [req]
  ;; (js/console.log "req" req)
  (let [data    (dispatch-pathom req)
        w       (t/writer :json)
        s       (t/write w data)
        headers (js-obj "Content-Type" "application/transit+json; charset=utf-8")]
    (println data)
    (.reply req 200 s headers)))
