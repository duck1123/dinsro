(ns dinsro.queries.ln.accounts
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.ln.accounts :as m.ln.accounts]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.specs]
   [xtdb.api :as xt]))

(def query-info
  {:ident   ::m.ln.accounts/id
   :pk      '?block-id
   :clauses [[:actor/id        '?actor-id]
             [:actor/admin?    '?admin?]
             [::m.c.wallets/id '?wallet-id]
             [::m.ln.nodes/id  '?network-id]]
   :rules
   (fn [[_actor-id admin? wallet-id network-id] rules]
     (->> rules
          (concat-when (not admin?)
            [['?ln-account-id      ::m.ln.accounts/node   '?ln-account-node-id]
             ['?ln-account-node-id ::m.ln.nodes/user      '?actor-id]])
          (concat-when wallet-id
            [['?ln-account-id      ::m.ln.accounts/wallet '?wallet-id]])
          (concat-when network-id
            [['?ln-account-id      ::m.ln.accounts/node   '?network-node-id]
             ['?network-node-id    ::m.ln.nodes/network   '?network-id]])))})

(defn count-ids
  ([] (count-ids {}))
  ([query-params] (c.xtdb/count-ids query-info query-params)))

(defn index-ids
  ([] (index-ids {}))
  ([query-params] (c.xtdb/index-ids query-info query-params)))

(>defn read-record
  [id]
  [:xt/id => (? ::m.ln.accounts/item)]
  (let [db     (c.xtdb/get-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.ln.accounts/id)
      (dissoc record :xt/id))))

(>defn create-record
  [params]
  [::m.ln.accounts/params => ::m.ln.accounts/id]
  (let [node            (c.xtdb/get-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.ln.accounts/id id)
                            (assoc :xt/id id))]
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    id))

(>defn delete!
  [id]
  [::m.ln.accounts/id => nil?]
  (let [node (c.xtdb/get-node)]
    (xt/await-tx node (xt/submit-tx node [[::xt/delete id]])))
  nil)
