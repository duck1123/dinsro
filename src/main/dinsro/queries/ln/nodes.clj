(ns dinsro.queries.ln.nodes
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.users :as m.users]
   [dinsro.specs]
   [xtdb.api :as xt]))

;; [../../actions/ln/nodes.clj]
;; [../../joins/ln/nodes.cljc]
;; [../../model/ln/nodes.cljc]

(def query-info
  {:ident   ::m.ln.nodes/id
   :pk      '?node-id
   :clauses [[::m.c.networks/id '?network-id]]
   :rules
   (fn [[network-id] rules]
     (->> rules
          (concat-when network-id
            [['?ln-node-id ::m.ln.nodes/network '?network-id]])))})

(defn count-ids
  ([] (count-ids {}))
  ([query-params] (c.xtdb/count-ids query-info query-params)))

(defn index-ids
  ([] (index-ids {}))
  ([query-params] (c.xtdb/index-ids query-info query-params)))

(>defn read-record
  [id]
  [:xt/id => (? ::m.ln.nodes/item)]
  (let [db     (c.xtdb/get-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.ln.nodes/id)
      (dissoc record :xt/id))))

(>defn create-record
  [params]
  [::m.ln.nodes/params => ::m.ln.nodes/id]
  (let [node            (c.xtdb/get-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.ln.nodes/id id)
                            (assoc :xt/id id))]
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    id))

(>defn update!
  [id data]
  [::m.ln.nodes/id any? => any?]
  (let [node   (c.xtdb/get-node)
        db     (c.xtdb/get-db)
        entity (xt/entity db id)
        new-data (merge entity data)
        tx     (xt/submit-tx node [[::xt/put new-data]])]
    (xt/await-tx node tx)))

(>defn find-by-user
  [user-id]
  [::m.users/id => (s/coll-of ::m.ln.nodes/id)]
  (c.xtdb/query-values
   '{:find  [?node-id]
     :in    [[?user-id]]
     :where [[?node-id ::m.ln.nodes/user ?user-id]]}
   [user-id]))

(>defn find-by-user-and-name
  [user-id name]
  [::m.users/id ::m.ln.nodes/name => (? ::m.ln.nodes/id)]
  (c.xtdb/query-value
   '{:find  [?node-id]
     :in    [[?user-id ?name]]
     :where [[?node-id ::m.ln.nodes/user ?user-id]
             [?node-id ::m.ln.nodes/name ?name]]}
   [user-id name]))

(>defn find-by-core-node
  [core-node-id]
  [::m.c.nodes/id => (s/coll-of ::m.ln.nodes/id)]
  (c.xtdb/query-values
   '{:find  [?node-id]
     :in    [[?core-node-id]]
     :where [[?node-id ::m.ln.nodes/core-node ?core-node-id]]}
   [core-node-id]))
