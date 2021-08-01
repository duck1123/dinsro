(ns dinsro.components.database-queries
  (:require
   [crux.api :as crux]
   [roterski.fulcro.rad.database-adapters.crux-options :as co]
   [taoensso.timbre :as log]))

(defn get-all-accounts-
  [db]
  (->> '{:find  [?uuid]
         :where [[?uuid :dinsro.model.accounts/id]]}
       (crux/q db)
       (mapv (fn [[id]] {:dinsro.model.accounts/id id}))))

(defn get-all-categories-
  [db]
  (->> '{:find  [?uuid]
         :where [[?uuid :dinsro.model.categories/id]]}
       (crux/q db)
       (mapv (fn [[id]] {:dinsro.model.categories/id id}))))

(defn get-all-currencies-
  [db]
  (->> '{:find  [?uuid]
         :where [[?uuid :dinsro.model.currencies/id]]}
       (crux/q db)
       (mapv (fn [[id]] {:dinsro.model.currencies/id id}))))

(defn get-all-navlinks-
  [db]
  (->> '{:find  [?id]
         :where [[?uuid :dinsro.model.navlink/id ?id]]}
       (crux/q db)
       (mapv (fn [[id]] {:dinsro.model.navlink/id id}))))

(defn get-all-rates-
  [db]
  (->> '{:find  [?uuid]
         :where [[?uuid :dinsro.model.rates/id]]}
       (crux/q db)
       (mapv (fn [[id]] {:dinsro.model.rates/id id}))))

(defn get-all-rate-sources-
  [db]
  (->> '{:find  [?uuid]
         :where [[?uuid :dinsro.model.rate-sources/id]]}
       (crux/q db)
       (mapv (fn [[id]] {:dinsro.model.rate-sources/id id}))))

(defn get-all-transactions-
  [db]
  (->> '{:find  [?uuid]
         :where [[?uuid :dinsro.model.transactions/id]]}
       (crux/q db)
       (mapv (fn [[id]] {:dinsro.model.transactions/id id}))))

(defn get-all-users-
  [db]
  (->> '{:find  [?uuid]
         :where [[?uuid :dinsro.model.users/id]]}
       (crux/q db)
       (mapv (fn [[id]] {:dinsro.model.users/id id}))))

(defn get-navlinks-
  [db ids]
  (let [query    '{:find  [?id]
                   :in    [[?id ...]]
                   :where [[?uuid :dinsro.model.navlink/id ?id]]}
        response (crux/q db query ids)]
    (mapv (fn [[id]] {:dinsro.model.navlink/id id}) response)))

(defn get-all-accounts
  [env _params]
  (if-let [db (some-> (get-in env [co/databases :production]) deref)]
    (get-all-accounts- db)
    (log/error "No database atom for production schema!")))

(defn get-all-categories
  [env _params]
  (if-let [db (some-> (get-in env [co/databases :production]) deref)]
    (get-all-categories- db)
    (log/error "No database atom for production schema!")))

(defn get-all-currencies
  [env _params]
  (if-let [db (some-> (get-in env [co/databases :production]) deref)]
    (get-all-currencies- db)
    (log/error "No database atom for production schema!")))

(defn get-all-navlinks
  [env _params]
  (if-let [db (some-> (get-in env [co/databases :production]) deref)]
    (get-all-navlinks- db)
    (log/error "No database atom for production schema!")))

(defn get-all-rates
  [env _params]
  (if-let [db (some-> (get-in env [co/databases :production]) deref)]
    (get-all-rates- db)
    (log/error "No database atom for production schema!")))

(defn get-all-rate-sources
  [env _params]
  (if-let [db (some-> (get-in env [co/databases :production]) deref)]
    (get-all-rate-sources- db)
    (log/error "No database atom for production schema!")))

(defn get-all-transactions
  [env _params]
  (if-let [db (some-> (get-in env [co/databases :production]) deref)]
    (get-all-transactions- db)
    (log/error "No database atom for production schema!")))

(defn get-all-users
  [env _params]
  (if-let [db (some-> (get-in env [co/databases :production]) deref)]
    (get-all-users- db)
    (log/error "No database atom for production schema!")))

(defn get-navlinks
  [env names]
  (if-let [db (some-> (get-in env [co/databases :production]) deref)]
    (get-navlinks- db names)
    (log/error "No database atom for production schema!")))
