(ns dinsro.queries.user-pubkeys
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.user-pubkeys :as m.user-pubkeys]
   [dinsro.specs]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

;; [[../actions/users.clj]]
;; [[../model/users.cljc]]
;; [[../ui/users.cljs]]

(def model-key ::m.user-pubkeys/id)

(def query-info
  {:ident        model-key
   :pk           '?user-pubkeys-id
   :clauses      [[:actor/id        '?actor-id]
                  [:actor/admin?    '?admin?]
                  [::m.n.pubkeys/id '?pubkey-id]]
   :sort-columns {}
   :rules
   (fn [[actor-id admin? pubkey-id] rules]
     (->> rules
          (concat-when (and (not admin?) actor-id)
            [['?user-pubkeys-id ::m.user-pubkeys/user   '?actor-id]])
          (concat-when pubkey-id
            [['?user-pubkeys-id ::m.user-pubkeys/pubkey '?pubkey-id]])))})

(defn count-ids
  ([] (count-ids {}))
  ([query-params] (c.xtdb/count-ids query-info query-params)))

(defn index-ids
  ([] (index-ids {}))
  ([query-params] (c.xtdb/index-ids query-info query-params)))

(>defn read-record
  [id]
  [uuid? => (? ::m.user-pubkeys/item)]
  (c.xtdb/read model-key  id))

(>defn create-record
  "Create a user record"
  [params]
  [::m.user-pubkeys/params => ::m.user-pubkeys/id]
  (c.xtdb/create! model-key params))

(>defn delete!
  [id]
  [:xt/id => nil?]
  (c.xtdb/delete! id))

(>defn update!
  [id data]
  [::m.user-pubkeys/id (s/keys) => ::m.user-pubkeys/id]
  (log/info :update!/starting {:id id :data data})
  (let [node   (c.xtdb/get-node)
        db     (c.xtdb/get-db)
        old    (xt/pull db '[*] id)
        params (merge old data)
        tx     (xt/submit-tx node [[::xt/put params]])]
    (xt/await-tx node tx)
    id))
