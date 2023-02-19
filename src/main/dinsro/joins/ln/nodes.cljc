(ns dinsro.joins.ln.nodes
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   #?(:clj [dinsro.actions.authentication :as a.authentication])
   [dinsro.model.core.transactions :as m.c.tx]
   [dinsro.model.ln.channels :as m.ln.channels]
   [dinsro.model.ln.invoices :as m.ln.invoices]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.ln.payments :as m.ln.payments]
   [dinsro.model.ln.payreqs :as m.ln.payreqs]
   [dinsro.model.ln.peers :as m.ln.peers]
   #?(:clj [dinsro.queries.core.tx :as q.c.tx])
   #?(:clj [dinsro.queries.ln.channels :as q.ln.channels])
   #?(:clj [dinsro.queries.ln.invoices :as q.ln.invoices])
   #?(:clj [dinsro.queries.ln.nodes :as q.ln.nodes])
   #?(:clj [dinsro.queries.ln.payments :as q.ln.payments])
   #?(:clj [dinsro.queries.ln.payreqs :as q.ln.payreqs])
   #?(:clj [dinsro.queries.ln.peers :as q.ln.peers])
   #?(:clj [dinsro.queries.users :as q.users])
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

#?(:clj
   (defn do-index
     [env]
     (if-let [user-id (a.authentication/get-user-id env)]
       (if (q.users/read-record user-id)
         (do
           (log/info :do-index/found-user {:user-id user-id})
           (q.ln.nodes/find-by-user user-id))
         (do
           (log/warn :do-index/user-not-found {:user-id user-id})
           (throw (RuntimeException. "user not found"))))
       (do
         (log/warn :do-index/no-user {:env env})
         []))))

(defattr index ::index :ref
  {ao/target    ::m.ln.nodes/id
   ao/pc-output [{::index [::m.ln.nodes/id]}]
   ao/pc-resolve
   (fn [env _]
     (comment env)
     (let [ids #?(:clj (do-index env) :cljs [])]
       (log/info :index/starting {:ids ids})
       {::index (m.ln.nodes/idents ids)}))})

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.ln.nodes/id
   ao/pc-output [{::admin-index [::m.ln.nodes/id]}]
   ao/pc-resolve
   (fn [_env _]
     (let [ids #?(:clj (q.ln.nodes/index-ids) :cljs [])]
       {::admin-index (m.ln.nodes/idents ids)}))})

(defattr channels ::channels :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.ln.nodes/id}
   ao/pc-output   [{::channels [::m.ln.channels/id]}]
   ao/target      ::m.ln.channels/id
   ao/pc-resolve
   (fn [_env {::m.ln.nodes/keys [id]}]
     (let [ids (if id #?(:clj (q.ln.channels/find-by-node id) :cljs []) [])]
       {::channels (m.ln.channels/idents ids)}))})

(defattr invoices ::invoices :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.ln.nodes/id}
   ao/pc-output   [{::invoices [::m.ln.invoices/id]}]
   ao/target      ::m.ln.invoices/id
   ao/pc-resolve
   (fn [_env {::m.ln.nodes/keys [id]}]
     (let [ids (if id #?(:clj (q.ln.invoices/find-by-node id) :cljs []) [])]
       {::invoices (m.ln.invoices/idents ids)}))})

(defattr payments ::payments :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.ln.nodes/id}
   ao/pc-output   [{::payments [::m.ln.payments/id]}]
   ao/target      ::m.ln.payments/id
   ao/pc-resolve
   (fn [_env {::m.ln.nodes/keys [id]}]
     (let [ids (if id #?(:clj (q.ln.payments/find-by-node id) :cljs []) [])]
       {::payments (m.ln.payments/idents ids)}))})

(defattr payreqs ::payreqs :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.ln.nodes/id}
   ao/pc-output   [{::payreqs [::m.ln.payreqs/id]}]
   ao/target      ::m.ln.payreqs/id
   ao/pc-resolve
   (fn [_env {::m.ln.nodes/keys [id]}]
     (let [ids (if id #?(:clj (q.ln.payreqs/find-by-node id) :cljs []) [])]
       {::payreqs (m.ln.payreqs/idents ids)}))})

(defattr peers ::peers :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.ln.nodes/id}
   ao/pc-output   [{::peers [::m.ln.peers/id]}]
   ao/target      ::m.ln.peers/id
   ao/pc-resolve
   (fn [_env {::m.ln.nodes/keys [id]}]
     (let [ids (if id #?(:clj (q.ln.peers/find-by-node id) :cljs []) [])]
       {::peers (m.ln.peers/idents ids)}))})

(defattr transactions ::transactions :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.ln.nodes/id}
   ao/pc-output   [{::transactions [::m.c.tx/id]}]
   ao/target      ::m.c.tx/id
   ao/pc-resolve
   (fn [_env {::m.ln.nodes/keys [id]}]
     (let [ids (if id #?(:clj (q.c.tx/find-by-ln-node id) :cljs []) [])]
       {::transactions (m.c.tx/idents (take 3 ids))}))})

(def attributes [admin-index index channels invoices payments payreqs peers transactions])
