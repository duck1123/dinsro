(ns dinsro.joins.categories
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   #?(:clj [dinsro.actions.authentication :as a.authentication])
   [dinsro.model.categories :as m.categories]
   [dinsro.model.transactions :as m.transactions]
   #?(:clj [dinsro.queries.categories :as q.categories])
   #?(:clj [dinsro.queries.transactions :as q.transactions])
   [dinsro.specs]))

(>def ::admin-index (s/coll-of (s/keys :req [::m.categories/id])))
(defattr admin-index ::admin-index :ref
  {ao/target    ::m.categories/id
   ao/pc-output [{::admin-index [::m.categories/id]}]
   ao/pc-resolve
   (fn [_env _]
     (let [ids #?(:clj (q.categories/index-ids) :cljs [])]
       {::admin-index (m.categories/idents ids)}))})

(>def ::index (s/coll-of (s/keys)))
(defattr index ::index :ref
  {ao/target    ::m.categories/id
   ao/pc-output [{::index [::m.categories/id]}]
   ao/pc-resolve
   (fn [env _]
     (comment env)
     (let [ids #?(:clj (if-let [user-id (a.authentication/get-user-id env)]
                         (q.categories/find-by-user user-id) [])
                  :cljs [])]
       {::index (m.categories/idents ids)}))})

(>def ::transactions (s/coll-of (s/keys :req [::m.transactions/id])))
(defattr transactions ::transactions :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.categories/id}
   ao/pc-output   [{::transactions [::m.transactions/id]}]
   ao/target      ::m.transactions/id
   ao/pc-resolve
   (fn [_env {::m.categories/keys [id]}]
     (let [ids (if id #?(:clj (q.transactions/find-by-category id) :cljs []) [])]
       {::transactions (m.categories/idents ids)}))})

(>def ::transaction-count number?)
(defattr transaction-count ::transaction-count :number
  {ao/pc-input   #{::transactions}
   ao/pc-resolve (fn [_ {::keys [transactions]}] {::transaction-count (count transactions)})})

(def attributes
  [admin-index
   index
   transaction-count
   transactions])
