(ns dinsro.queries.categories
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.users :as m.users]
   [dinsro.specs]
   [xtdb.api :as xt]))

;; [../actions/categories.clj]
;; [../model/categories.cljc]

(def query-info
  {:ident   ::m.categories/id
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

(>defn create-record
  [params]
  [::m.categories/params => :xt/id]
  (let [node   (c.xtdb/get-node)
        id     (new-uuid)
        params (assoc params ::m.categories/id id)
        params (assoc params :xt/id id)]
    (xt/await-tx node (xt/submit-tx node [[::xt/put params]]))
    id))

(>defn read-record
  [id]
  [:xt/id => (? ::m.categories/item)]
  (let [db     (c.xtdb/get-db)
        record (xt/pull db '[*] id)]
    ;; FIXME: This is doing too much
    (when (get record ::m.categories/name)
      (let [user-id (get-in record [::m.categories/user :xt/id])]
        (-> record
            (dissoc :xt/id)
            (assoc ::m.categories/user {::m.users/id user-id}))))))

(>defn delete!
  [id]
  [:xt/id => nil?]
  (c.xtdb/delete! id))
