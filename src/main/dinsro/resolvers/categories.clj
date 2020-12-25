(ns dinsro.resolvers.categories
  (:require
   [com.wsscode.pathom.connect :as pc :refer [defresolver]]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.users :as m.users]
   [dinsro.sample :as sample]
   [taoensso.timbre :as timbre]))

(defresolver categories-resolver
  [_env _props]
  {::pc/output [{:all-categories [::m.categories/id]}]}
  {:all-categories (map (fn [id] [::m.categories/id id])
                        (keys sample/category-map))})

(defresolver category-resolver
  [_env {::m.categories/keys [id]}]
  {::pc/input #{::m.categories/id}
   ::pc/output [::m.categories/name
                {::m.categories/user [::m.users/id]}]}
  (get sample/category-map id))

(defresolver category-map-resolver
  [_env _props]
  {::pc/output [::m.categories/map]}
  {::m.categories/map sample/category-map})

(def resolvers [category-resolver categories-resolver category-map-resolver])
