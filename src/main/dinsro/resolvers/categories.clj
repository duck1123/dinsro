(ns dinsro.resolvers.categories
  (:require
   [com.wsscode.pathom.connect :as pc :refer [defresolver]]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.users :as m.users]
   [dinsro.queries.categories :as q.categories]
   [taoensso.timbre :as timbre]))

(defn resolve-categories
  []
  {:all-categories
   (map m.categories/ident (q.categories/index-ids))})

(defn resolve-category
  [id]
  (let [record   (q.categories/read-record id)
        id       (:db/id record)
        user-eid (get-in record [::m.categories/user :db/id])]
    (-> record
        (assoc ::m.categories/id id)
        (assoc ::m.categories/user [[::m.users/id user-eid]]))))

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
