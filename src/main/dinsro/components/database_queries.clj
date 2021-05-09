(ns dinsro.components.database-queries
  (:require
   [crux.api :as crux]
   [roterski.fulcro.rad.database-adapters.crux-options :as co]
   [taoensso.encore :as enc]
   [taoensso.timbre :as log]))

(defn get-all-currencies
  [env _params]
  (if-let [db (some-> (get-in env [co/databases :production]) deref)]
    (->> '{:find  [?uuid]
           :where [[?uuid :account/id]]}
         (crux/q db)
         (mapv (fn [[id]] {:account/id id})))
    (log/error "No database atom for production schema!"))
  )
