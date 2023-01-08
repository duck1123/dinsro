(ns dinsro.joins.nostr.events
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   #?(:clj [dinsro.actions.authentication :as a.authentication])
   [dinsro.model.nostr.events :as m.n.events]
   #?(:clj [dinsro.queries.nostr.events :as q.n.events])
   #?(:clj [dinsro.queries.users :as q.users])
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

#?(:clj
   (defn do-index
     [env]
     (if-let [user-id (a.authentication/get-user-id env)]
       (if-let [user (q.users/read-record user-id)]
         (do
           (log/info :do-index/found-user {:user-id user-id :user user})
           [])
         (do
           (log/warn :do-index/user-not-found {:user-id user-id})
           (throw (RuntimeException. "user not found"))))
       (do
         (log/warn :do-index/no-user {:env env})
         []))))

(defattr index ::m.n.events/index :ref
  {ao/target    ::m.n.events/id
   ao/pc-output [{::m.n.events/index [::m.n.events/id]}]
   ao/pc-resolve
   (fn [env _]
     (comment env)
     (let [ids #?(:clj (do-index env) :cljs [])]
       (log/info :index/starting {:ids ids})
       {::m.n.events/index (m.n.events/idents ids)}))})

(defattr admin-index ::m.n.events/admin-index :ref
  {ao/target    ::m.n.events/id
   ao/pc-output [{::m.n.events/admin-index [::m.n.events/id]}]
   ao/pc-resolve
   (fn [_env _]
     (let [ids #?(:clj (q.n.events/index-ids) :cljs [])]
       {::m.n.events/admin-index (m.n.events/idents ids)}))})

(def attributes
  [admin-index
   index])
