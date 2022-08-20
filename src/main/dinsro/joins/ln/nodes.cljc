(ns dinsro.joins.ln.nodes
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   #?(:clj [dinsro.actions.authentication :as a.authentication])
   [dinsro.model.ln.channels :as m.ln.channels]
   [dinsro.model.ln.invoices :as m.ln.invoices]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.ln.payments :as m.ln.payments]
   [dinsro.model.ln.payreqs :as m.ln.payreqs]
   [dinsro.model.ln.peers :as m.ln.peers]
   [dinsro.model.ln.transactions :as m.ln.tx]
   [dinsro.model.transactions :as m.transactions]
   #?(:clj [dinsro.queries.ln.channels :as q.ln.channels])
   #?(:clj [dinsro.queries.ln.invoices :as q.ln.invoices])
   #?(:clj [dinsro.queries.ln.nodes :as q.ln.nodes])
   #?(:clj [dinsro.queries.ln.payments :as q.ln.payments])
   #?(:clj [dinsro.queries.ln.payreqs :as q.ln.payreqs])
   #?(:clj [dinsro.queries.ln.peers :as q.ln.peers])
   #?(:clj [dinsro.queries.ln.transactions :as q.ln.tx])
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

(defattr index ::m.ln.nodes/index :ref
  {ao/target    ::m.ln.nodes/id
   ao/pc-output [{::m.ln.nodes/index [::m.ln.nodes/id]}]
   ao/pc-resolve
   (fn [{:keys [query-params] :as env} _]
     (let [ids #?(:clj
                  (if-let [user-id (a.authentication/get-user-id env)]
                    (do
                      (log/info :index/found-user {:user-id user-id})
                      (q.ln.nodes/find-by-user user-id))
                    (do
                      (log/warn :index/no-user {:env env})
                      []))
                  :cljs [])]
       (log/info :index/starting {:ids ids})
       (comment env query-params)
       (let [idents (m.ln.nodes/idents ids)]
         (log/info :index/finished {:idents idents})
         {::m.ln.nodes/index idents})))})

(defattr admin-index ::m.ln.nodes/admin-index :ref
  {ao/target    ::m.ln.nodes/id
   ao/pc-output [{::m.ln.nodes/admin-index [::m.ln.nodes/id]}]
   ao/pc-resolve
   (fn [_env _]
     (let [ids #?(:clj (q.ln.nodes/index-ids) :cljs [])]
       {::m.ln.nodes/admin-index (m.ln.nodes/idents ids)}))})

(defattr channels ::m.ln.nodes/channels :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.ln.nodes/id}
   ao/pc-output   [{::m.ln.nodes/channels [::m.ln.channels/id]}]
   ao/target      ::m.ln.channels/id
   ao/pc-resolve
   (fn [_env {::m.ln.nodes/keys [id]}]
     (let [ids (if id #?(:clj (q.ln.channels/find-ids-by-node id) :cljs []) [])]
       {::m.ln.nodes/channels (m.ln.channels/idents ids)}))})

(defattr invoices ::m.ln.nodes/invoices :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.ln.nodes/id}
   ao/pc-output   [{::m.ln.nodes/invoices [::m.ln.invoices/id]}]
   ao/target      ::m.ln.invoices/id
   ao/pc-resolve
   (fn [_env {::m.ln.nodes/keys [id]}]
     (let [ids (if id #?(:clj (q.ln.invoices/find-ids-by-node id) :cljs []) [])]
       {::m.ln.nodes/invoices (m.ln.invoices/idents ids)}))})

(defattr payments ::m.ln.nodes/payments :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.ln.nodes/id}
   ao/pc-output   [{::m.ln.nodes/payments [::m.ln.payments/id]}]
   ao/target      ::m.ln.payments/id
   ao/pc-resolve
   (fn [_env {::m.ln.nodes/keys [id]}]
     (let [ids (if id #?(:clj (q.ln.payments/find-ids-by-node id) :cljs []) [])]
       {::m.ln.nodes/payments (m.ln.payments/idents ids)}))})

(defattr payreqs ::m.ln.nodes/payreqs :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.ln.nodes/id}
   ao/pc-output   [{::m.ln.nodes/payreqs [::m.ln.payreqs/id]}]
   ao/target      ::m.ln.payreqs/id
   ao/pc-resolve
   (fn [_env {::m.ln.nodes/keys [id]}]
     (let [ids (if id #?(:clj (q.ln.payreqs/find-ids-by-node id) :cljs []) [])]
       {::m.ln.nodes/payreqs (m.ln.payreqs/idents ids)}))})

(defattr peers ::m.ln.nodes/peers :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.ln.nodes/id}
   ao/pc-output   [{::m.ln.nodes/peers [::m.ln.peers/id]}]
   ao/target      ::m.ln.peers/id
   ao/pc-resolve
   (fn [_env {::m.ln.nodes/keys [id]}]
     (let [ids (if id #?(:clj (q.ln.peers/find-ids-by-node id) :cljs []) [])]
       {::m.ln.nodes/peers (m.ln.peers/idents ids)}))})

(defattr transactions ::m.ln.nodes/transactions :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.ln.nodes/id}
   ao/pc-output   [{::m.ln.nodes/transactions [::m.ln.tx/id]}]
   ao/target      ::m.ln.tx/id
   ao/pc-resolve
   (fn [_env {::m.ln.nodes/keys [id]}]
     (let [ids (if id #?(:clj (q.ln.tx/find-ids-by-node id) :cljs []) [])]
       {::m.ln.nodes/transactions (m.transactions/idents (take 3 ids))}))})

(def attributes
  [admin-index
   index
   channels
   invoices
   payments
   payreqs
   peers
   transactions])
