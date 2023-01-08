(ns dinsro.joins.contacts
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   #?(:clj [dinsro.actions.authentication :as a.authentication])
   [dinsro.model.contacts :as m.contacts]
   #?(:clj [dinsro.queries.contacts :as q.contacts])
   [dinsro.specs]))

(defattr index ::m.contacts/index :ref
  {ao/target    ::m.contacts/id
   ao/pc-output [{::m.contacts/index [::m.contacts/id]}]
   ao/pc-resolve
   (fn [env _]
     (comment env)
     (let [ids #?(:clj (if-let [user-id (a.authentication/get-user-id env)]
                         (q.contacts/find-by-user user-id) [])
                  :cljs [])]
       {::m.contacts/index (m.contacts/idents ids)}))})

(defattr admin-index ::m.contacts/admin-index :ref
  {ao/target    ::m.contacts/id
   ao/pc-output [{::m.contacts/admin-index [::m.contacts/id]}]
   ao/pc-resolve
   (fn [_env _]
     (let [ids #?(:clj (q.contacts/index-ids) :cljs [])]
       {::m.contacts/admin-index (m.contacts/idents ids)}))})

(def attributes [admin-index index])
