(ns dinsro.queries.core.mnemonics
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.core.mnemonics :as m.c.mnemonics]
   [dinsro.specs]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

(def query-info
  {:ident   ::m.c.mnemonics/id
   :pk      '?mnemonic-id
   :clauses [[:actor/id     '?actor-id]
             [:actor/admin? '?admin?]]
   :rules   (fn [[_actor-id admin?] rules]
              (->> rules
                   (concat-when (not admin?)
                     [['?mnemonic-id ::m.c.mnemonics/user '?actor-id]])))})

(defn count-ids
  ([] (count-ids {}))
  ([query-params] (c.xtdb/count-ids query-info query-params)))

(defn index-ids
  ([] (index-ids {}))
  ([query-params] (c.xtdb/index-ids query-info query-params)))

(>defn read-record
  [id]
  [:xt/id => (? ::m.c.mnemonics/item)]
  (let [db     (c.xtdb/get-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.c.mnemonics/id)
      (dissoc record :xt/id))))

(>defn create-record
  [params]
  [::m.c.mnemonics/params => ::m.c.mnemonics/id]
  (let [node            (c.xtdb/get-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.c.mnemonics/id id)
                            (assoc :xt/id id))]
    (log/debug :create-record/starting {:params prepared-params})
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    (log/trace :create-record/finished {:id id})
    id))
