(ns dinsro.resolvers.categories
  (:require
   [com.wsscode.pathom.connect :as pc :refer [defresolver]]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.users :as m.users]
   [dinsro.queries.categories :as q.categories]
   [taoensso.timbre :as timbre]))

(defresolver categories-resolver
  [_env _props]
  {::pc/output [{:all-categories [::m.categories/id]}]}
  {:all-categories
   (map (fn [id] [::m.categories/id id]) (q.categories/index-ids))})

(defresolver category-resolver
  [_env {::m.categories/keys [id]}]
  {::pc/input #{::m.categories/id}
   ::pc/output [::m.categories/name
                {::m.categories/user [::m.users/id]}]}
  (let [record (q.categories/read-record id)
        id (:db/id record)]
    (assoc record ::m.categories/id id)))

(def resolvers [category-resolver categories-resolver])
