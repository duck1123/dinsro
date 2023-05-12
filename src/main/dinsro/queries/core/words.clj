(ns dinsro.queries.core.words
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.core.mnemonics :as m.c.mnemonics]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.core.words :as m.c.words]
   [dinsro.specs]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

(def query-info
  {:ident   ::m.c.words/id
   :pk      '?word-id
   :clauses [[:actor/id            '?actor-id]
             [:actor/admin?        '?admin?]
             [::m.c.mnemonics/id   '?mnemonic-id]
             [::m.c.words/position '?position]]
   :rules
   (fn [[_actor-id admin?
         mnemonic-id position] rules]
     (->> rules
          (concat-when (not admin?)
            [['?word-id          ::m.c.words/mnemonic '?auth-mnemonic-id]
             ['?auth-mnemonic-id ::m.c.mnemonics/user '?actor-id]])
          (concat-when mnemonic-id
            [['?word-id          ::m.c.words/mnemonic '?mnemonic-id]])
          (concat-when position
            [['?word-id          ::m.c.words/position '?position]])))})

(defn count-ids
  ([] (count-ids {}))
  ([query-params] (c.xtdb/count-ids query-info query-params)))

(defn index-ids
  ([] (index-ids {}))
  ([query-params] (c.xtdb/index-ids query-info query-params)))

(>defn read-record
  [id]
  [::m.c.words/id => (? ::m.c.words/item)]
  (let [db     (c.xtdb/get-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.c.words/id)
      (dissoc record :xt/id))))

(>defn create-record
  [params]
  [::m.c.words/params => ::m.c.words/id]
  (s/assert ::m.c.words/params params)
  (let [node            (c.xtdb/get-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.c.words/id id)
                            (assoc :xt/id id))]
    (log/trace :create-record/starting {:params prepared-params})
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    (log/trace :create-record/finished {:id id})
    id))

(>defn find-by-wallet
  [wallet-id]
  [::m.c.wallets/id => (s/coll-of ::m.c.words/id)]
  (c.xtdb/query-values
   '{:find  [?word-id]
     :in    [[?wallet-id]]
     :where [[?word-id ::m.c.words/mnemonic ?mnemonic-id]
             [?wallet-id ::m.c.wallets/mnemonic ?mnemonic-id]]}
   [wallet-id]))

(>defn delete!
  [id]
  [:xt/id => nil?]
  (let [node (c.xtdb/get-node)]
    (xt/await-tx node (xt/submit-tx node [[::xt/delete id]]))
    nil))
