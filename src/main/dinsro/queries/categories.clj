(ns dinsro.queries.categories
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.users :as m.users]
   [dinsro.specs]))

;; [[../actions/categories.clj]]
;; [[../joins/categories.cljc]]
;; [[../model/categories.cljc]]

(def model-key ::m.categories/id)

(def query-info
  {:ident   model-key
   :pk      '?category-id
   :clauses [[:actor/id     '?actor-id]
             [:actor/admin? '?admin?]
             [::m.users/id  '?user-id]]
   :rules
   (fn [[actor-id admin? user-id] rules]
     (->> rules
          (concat-when (and (not admin?) actor-id)
            [['?category-id ::m.categories/user '?actor-id]])
          (concat-when user-id
            [['?category-id ::m.categories/user '?user-id]])))})

(defn count-ids
  ([] (count-ids {}))
  ([query-params] (c.xtdb/count-ids query-info query-params)))

(defn index-ids
  ([] (index-ids {}))
  ([query-params] (c.xtdb/index-ids query-info query-params)))

(>defn find-by-user
  [user-id]
  [::m.users/id => (s/coll-of ::m.categories/id)]
  (c.xtdb/query-values
   '{:find  [?category-id]
     :in    [[?user-id]]
     :where [[?category-id ::m.categories/user ?user-id]]}
   [user-id]))

(>defn create!
  [params]
  [::m.categories/params => :xt/id]
  (c.xtdb/create! model-key params))

(>defn read-record
  [id]
  [:xt/id => (? ::m.categories/item)]
  (c.xtdb/read model-key id))

(>defn delete!
  [id]
  [:xt/id => nil?]
  (c.xtdb/delete! id))
