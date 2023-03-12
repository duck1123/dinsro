(ns dinsro.components.database-queries
  (:require
   [dinsro.components.xtdb]
   [dinsro.queries :as q]
   [lambdaisland.glogc :as log]
   [mount.core :refer [defstate]]
   [roterski.fulcro.rad.database-adapters.xtdb-options :as co]
   [taoensso.encore :as enc]
   [xtdb.api :as xt]))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defstate ^{:on-reload :noop} queries
  "A collection of started xtdb nodes"
  :start (do
           (log/info :queries/starting {})
           (q/initialize-queries!))
  :stop (log/info :queries/stopping {}))

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
