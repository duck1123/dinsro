(ns dinsro.queries.categories
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.users :as m.users]
   [dinsro.specs]
   [xtdb.api :as xt]))

(>defn find-by-user
  [user-id]
  [::m.users/id => (s/coll-of ::m.categories/id)]
  (c.xtdb/query-ids
   '{:find  [?category-id]
     :in    [[?user-id]]
     :where [[?category-id ::m.categories/user ?user-id]]}
   [user-id]))

(>defn create-record
  [params]
  [::m.categories/params => :xt/id]
  (let [node   (c.xtdb/main-node)
        id     (new-uuid)
        params (assoc params ::m.categories/id id)
        params (assoc params :xt/id id)]
    (xt/await-tx node (xt/submit-tx node [[::xt/put params]]))
    id))

(>defn read-record
  [id]
  [:xt/id => (? ::m.categories/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    ;; FIXME: This is doing too much
    (when (get record ::m.categories/name)
      (let [user-id (get-in record [::m.categories/user :xt/id])]
        (-> record
            (dissoc :xt/id)
            (assoc ::m.categories/user {::m.users/id user-id}))))))

(>defn index-ids
  []
  [=> (s/coll-of :xt/id)]
  (c.xtdb/query-ids '{:find  [?e]
                      :where [[?e ::m.categories/name _]]}))

(>defn delete-record
  [id]
  [:xt/id => nil?]
  (let [node (c.xtdb/main-node)]
    (xt/await-tx node (xt/submit-tx node [[::xt/delete id]])))
  nil)

