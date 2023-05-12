(ns dinsro.queries.core.wallets
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.core.mnemonics :as m.c.mnemonics]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.users :as m.users]
   [dinsro.specs]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

(def query-info
  {:ident   ::m.c.wallets/id
   :pk      '?wallet-id
   :clauses [[:actor/id          '?actor-id]
             [:actor/admin?      '?admin?]
             [::m.c.mnemonics/id '?mnemonic-id]
             [::m.c.networks/id  '?network-id]]
   :rules
   (fn [[_actor-id admin? mnemonic-id network-id] rules]
     (->> rules
          (concat-when (not admin?)
            [['?wallet-id        ::m.c.wallets/user     '?actor-id]])
          (concat-when mnemonic-id
            [['?wallet-id        ::m.c.wallets/mnemonic '?mnemonic-id]])
          (concat-when network-id
            [['?wallet-id        ::m.c.wallets/network  '?network-id]])))})

(defn count-ids
  ([] (count-ids {}))
  ([query-params] (c.xtdb/count-ids query-info query-params)))

(defn index-ids
  ([] (index-ids {}))
  ([query-params] (c.xtdb/index-ids query-info query-params)))

(>defn read-record
  [id]
  [:xt/id => (? ::m.c.wallets/item)]
  (let [db     (c.xtdb/get-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.c.wallets/id)
      (dissoc record :xt/id))))

(>defn create-record
  [params]
  [::m.c.wallets/params => ::m.c.wallets/id]
  (let [node            (c.xtdb/get-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.c.wallets/id id)
                            (assoc :xt/id id))]
    (log/debug :create-record/starting {:params prepared-params})
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    (log/trace :create-record/finished {:id id})
    id))

(>defn index-records
  []
  [=> (s/coll-of ::m.c.wallets/item)]
  (map read-record (index-ids)))

(>defn find-by-user
  [user-id]
  [::m.users/id => (s/coll-of ::m.c.wallets/id)]
  (c.xtdb/query-values
   '{:find  [?wallet-id]
     :in    [[?user-id]]
     :where [[?wallet-id ::m.c.wallets/user ?user-id]]}
   [user-id]))

(>defn find-by-user-and-name
  [user-id name]
  [::m.users/id string? => (? ::m.c.wallets/id)]
  (c.xtdb/query-value
   '{:find  [?wallet-id]
     :in    [[?user-id ?name]]
     :where [[?wallet-id ::m.c.wallets/user ?user-id]
             [?wallet-id ::m.c.wallets/name ?name]]}
   [user-id name]))

(>defn find-by-user-and-wallet-id
  [user-id wallet-id]
  [::m.users/id ::m.c.wallets/id => (? ::m.c.wallets/id)]
  (c.xtdb/query-value
   '{:find [?wallet-id]
     :in [[?user-id ?input-wallet-id]]
     :where [[?wallet-id ::m.c.wallets/id ?input-wallet-id]
             [?wallet-id ::m.c.wallets/user ?user-id]]}
   [user-id wallet-id]))

(>defn find-by-core-node
  [node-id]
  [::m.c.nodes/id => (s/coll-of ::m.c.wallets/id)]
  (log/info :find-by-core-node/starting {:node-id node-id})
  (c.xtdb/query-values
   '{:find  [?wallet-id]
     :in    [[?node-id]]
     :where [[?wallet-id ::m.c.wallets/node ?node-id]]}
   [node-id]))

(defn update!
  [wallet-id new-props]
  (log/info :update!/starting {:wallet-id wallet-id :new-props new-props})
  (let [node          (c.xtdb/get-node)
        wallet        (read-record wallet-id)
        updated-props (merge wallet
                             {:xt/id wallet-id}
                             new-props)
        tx            (xt/submit-tx node [[::xt/put updated-props]])]
    (xt/await-tx node tx)
    wallet-id))
