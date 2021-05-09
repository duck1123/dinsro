(ns dinsro.components.database-queries
  (:require
   [crux.api :as crux]
   ;; [dinsro.model.accounts :as m.accounts]
   ;; [dinsro.model.categories :as m.categories]
   ;; [dinsro.model.currencies :as m.currencies]
   ;; [dinsro.model.rates :as m.rates]
   ;; [dinsro.model.rate-sources :as m.rate-sources]
   ;; [dinsro.model.transactions :as m.transactions]
   ;; [dinsro.model.users :as m.users]
   [roterski.fulcro.rad.database-adapters.crux-options :as co]
   [taoensso.encore :as enc]
   [taoensso.timbre :as log]))

(defn get-all-accounts
  [env _params]
  (if-let [db (some-> (get-in env [co/databases :production]) deref)]
    (->> '{:find  [?uuid]
           :where [[?uuid :dinsro.model.accounts/id]]}
         (crux/q db)
         (mapv (fn [[id]] {:dinsro.model.accounts/id id})))
    (log/error "No database atom for production schema!")))

(defn get-all-categories
  [env _params]
  (if-let [db (some-> (get-in env [co/databases :production]) deref)]
    (->> '{:find  [?uuid]
           :where [[?uuid :dinsro.model.categories/id]]}
         (crux/q db)
         (mapv (fn [[id]] {:dinsro.model.categories/id id})))
    (log/error "No database atom for production schema!")))

(defn get-all-currencies
  [env _params]
  (if-let [db (some-> (get-in env [co/databases :production]) deref)]
    (->> '{:find  [?uuid]
           :where [[?uuid :dinsro.model.currencies/id]]}
         (crux/q db)
         (mapv (fn [[id]] {:dinsro.model.currencies/id id})))
    (log/error "No database atom for production schema!")))

(defn get-all-rates
  [env _params]
  (if-let [db (some-> (get-in env [co/databases :production]) deref)]
    (->> '{:find  [?uuid]
           :where [[?uuid :dinsro.model.rates/id]]}
         (crux/q db)
         (mapv (fn [[id]] {:dinsro.model.rates/id id})))
    (log/error "No database atom for production schema!")))

(defn get-all-rate-sources
  [env _params]
  (if-let [db (some-> (get-in env [co/databases :production]) deref)]
    (->> '{:find  [?uuid]
           :where [[?uuid :dinsro.model.rate-sources/id]]}
         (crux/q db)
         (mapv (fn [[id]] {:dinsro.model.rate-sources/id id})))
    (log/error "No database atom for production schema!")))

(defn get-all-transactions
  [env _params]
  (if-let [db (some-> (get-in env [co/databases :production]) deref)]
    (->> '{:find  [?uuid]
           :where [[?uuid :dinsro.model.transactions/id]]}
         (crux/q db)
         (mapv (fn [[id]] {:dinsro.model.transactions/id id})))
    (log/error "No database atom for production schema!")))

(defn get-all-users
  [env _params]
  (if-let [db (some-> (get-in env [co/databases :production]) deref)]
    (->> '{:find  [?uuid]
           :where [[?uuid :dinsro.model.users/id]]}
         (crux/q db)
         (mapv (fn [[id]] {:dinsro.model.users/id id})))
    (log/error "No database atom for production schema!")))

(comment
  (require 'dinsro.components.crux)
  (:main dinsro.components.crux/crux-nodes)

  (def db (crux/db (:main dinsro.components.crux/crux-nodes)))

  (crux/q db '{:find [?uuid] :where [[?uuid :dinsro.model.accounts/id]]})
  )
