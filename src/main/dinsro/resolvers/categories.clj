(ns dinsro.resolvers.categories
  (:require
   [com.wsscode.pathom.connect :as pc :refer [defresolver]]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.users :as m.users]
   [dinsro.queries.categories :as q.categories]
   [taoensso.timbre :as log]))

(defn resolve-categories
  []
  (let [records (q.categories/index-records)
        idents  (map m.categories/ident-item records)]
    {:all-categories idents}))

(defn resolve-category
  [id]
  (let [eid     (q.categories/find-eid-by-id id)
        record  (q.categories/read-record eid)
        user-id (get-in record [::m.categories/user ::m.users/id])]
    (-> record
        (assoc ::m.categories/id id)
        (assoc ::m.categories/user [(m.users/ident user-id)]))))

(defn resolve-category-link
  [id]
  {::m.categories/link [(m.categories/ident id)]})

(defresolver categories-resolver
  [_env _props]
  {::pc/output [{:all-categories [::m.categories/id]}]}
  (resolve-categories))

(defresolver category-resolver
  [_env {::m.categories/keys [id]}]
  {::pc/input  #{::m.categories/id}
   ::pc/output [::m.categories/name
                {::m.categories/user [::m.users/id]}]}
  (resolve-category id))

(defresolver category-link-resolver
  [_env {::m.categories/keys [id]}]
  {::pc/input  #{::m.categories/id}
   ::pc/output [{::m.categories/link [::m.categories/id]}]}
  (resolve-category-link id))

(def resolvers
  [category-resolver
   category-link-resolver
   categories-resolver])
