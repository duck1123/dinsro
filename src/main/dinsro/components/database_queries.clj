(ns dinsro.components.database-queries
  (:require
   [xtdb.api :as xt]
   [roterski.fulcro.rad.database-adapters.xtdb-options :as co]
   [taoensso.encore :as enc]
   [taoensso.timbre :as log]))

(defn get-all-accounts-
  [db]
  (->> '{:find  [?uuid]
         :where [[?uuid :dinsro.model.accounts/id]]}
       (xt/q db)
       (mapv (fn [[id]] {:dinsro.model.accounts/id id}))))

(defn get-my-accounts-
  [db user-id]
  (let [query    '{:find  [?uuid]
                   :in    [?user-id]
                   :where [[?uuid :dinsro.model.accounts/user ?user-id]]}
        response (xt/q db query user-id)]
    (mapv (fn [[id]] {:dinsro.model.accounts/id id}) response)))

(defn get-all-categories-
  [db]
  (->> '{:find  [?uuid]
         :where [[?uuid :dinsro.model.categories/id]]}
       (xt/q db)
       (mapv (fn [[id]] {:dinsro.model.categories/id id}))))

(defn get-all-core-nodes-
  [db]
  (->> '{:find  [?uuid]
         :where [[?uuid :dinsro.model.core-nodes/id]]}
       (xt/q db)
       (mapv (fn [[id]] {:dinsro.model.core-nodes/id id}))))

(defn get-all-lightning-nodes-
  [db]
  (->> '{:find  [?uuid]
         :where [[?uuid :dinsro.model.ln-nodes/id]]}
       (xt/q db)
       (mapv (fn [[id]] {:dinsro.model.ln-nodes/id id}))))

(defn get-all-currencies-
  [db]
  (->> '{:find  [?uuid]
         :where [[?uuid :dinsro.model.currencies/id]]}
       (xt/q db)
       (mapv (fn [[id]] {:dinsro.model.currencies/id id}))))

(defn get-all-navlinks-
  [db]
  (->> '{:find  [?id]
         :where [[?uuid :dinsro.model.navlink/id ?id]]}
       (xt/q db)
       (mapv (fn [[id]] {:dinsro.model.navlink/id id}))))

(defn get-all-ln-peers-
  [db]
  (->> '{:find  [?uuid]
         :where [[?uuid :dinsro.model.ln-peers/id]]}
       (xt/q db)
       (mapv (fn [[id]] {:dinsro.model.ln-peers/id id}))))

(defn get-all-rates-
  [db]
  (->> '{:find  [?uuid]
         :where [[?uuid :dinsro.model.rates/id]]}
       (xt/q db)
       (mapv (fn [[id]] {:dinsro.model.rates/id id}))))

(defn get-all-rate-sources-
  [db]
  (->> '{:find  [?uuid]
         :where [[?uuid :dinsro.model.rate-sources/id]]}
       (xt/q db)
       (mapv (fn [[id]] {:dinsro.model.rate-sources/id id}))))

(defn get-all-transactions-
  [db]
  (->> '{:find  [?uuid]
         :where [[?uuid :dinsro.model.transactions/id]]}
       (xt/q db)
       (mapv (fn [[id]] {:dinsro.model.transactions/id id}))))

(defn get-all-ln-transactions-
  [db]
  (->> '{:find  [?uuid]
         :where [[?uuid :dinsro.model.ln-transactions/id]]}
       (xt/q db)
       (mapv (fn [[id]] {:dinsro.model.ln-transactions/id id}))))

(defn get-all-users-
  [db]
  (->> '{:find  [?uuid]
         :where [[?uuid :dinsro.model.users/id]]}
       (xt/q db)
       (mapv (fn [[id]] {:dinsro.model.users/id id}))))

(defn get-navlinks-
  [db ids]
  (let [query    '{:find  [?id]
                   :in    [[?id ...]]
                   :where [[?uuid :dinsro.model.navlink/id ?id]]}
        response (xt/q db query ids)]
    (mapv (fn [[id]] {:dinsro.model.navlink/id id}) response)))

(defn get-all-accounts
  [env _params]
  (if-let [db (some-> (get-in env [co/databases :production]) deref)]
    (get-all-accounts- db)
    (log/error "No database atom for production schema!")))

(defn get-my-accounts
  [env user-id _params]
  (if-let [db (some-> (get-in env [co/databases :production]) deref)]
    (get-my-accounts- db user-id)
    (log/error "No database atom for production schema!")))

(defn get-all-categories
  [env _params]
  (if-let [db (some-> (get-in env [co/databases :production]) deref)]
    (get-all-categories- db)
    (log/error "No database atom for production schema!")))

(defn get-all-core-nodes
  [env _params]
  (if-let [db (some-> (get-in env [co/databases :production]) deref)]
    (get-all-core-nodes- db)
    (log/error "No database atom for production schema!")))

(defn get-all-lightning-nodes
  [env _params]
  (if-let [db (some-> (get-in env [co/databases :production]) deref)]
    (get-all-lightning-nodes- db)
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

(defn get-all-ln-transactions
  [env _params]
  (if-let [db (some-> (get-in env [co/databases :production]) deref)]
    (get-all-ln-transactions- db)
    (log/error "No database atom for production schema!")))

(defn get-all-ln-peers
  [env _params]
  (if-let [db (some-> (get-in env [co/databases :production]) deref)]
    (get-all-ln-peers- db)
    (log/error "No database atom for production schema!")))

(defn get-all-users
  [env _params]
  (if-let [db (some-> (get-in env [co/databases :production]) deref)]
    (get-all-users- db)
    (log/error "No database atom for production schema!")))

(defn get-login-info
  "Get the account name, time zone, and password info via a username (email)."
  [env username]
  (enc/if-let [db (some-> (get-in env [co/databases :production]) deref)]
    (let [query '{:find  [(pull ?user-id
                                [:dinsro.model.users/id
                                 :dinsro.model.users/name
                                 {:time-zone/zone-id [:xt/id]}
                                 :dinsro.model.users/hashed-value
                                 :dinsro.model.users/salt
                                 :dinsro.model.users/iterations])]
                  :in    [?username]
                  :where [[?user-id :dinsro.model.users/name ?username]]}]
      (ffirst (xt/q db query username)))))

(defn get-navlinks
  [env names]
  (if-let [db (some-> (get-in env [co/databases :production]) deref)]
    (get-navlinks- db names)
    (log/error "No database atom for production schema!")))
