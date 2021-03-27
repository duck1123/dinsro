(ns dinsro.resolvers.accounts
  (:require
   [com.wsscode.pathom.connect :as pc :refer [defresolver]]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.users :as m.users]
   [dinsro.queries.accounts :as q.accounts]
   [dinsro.sample :as sample]
   [taoensso.timbre :as timbre]))

(defresolver account-resolver
  [_env {::m.accounts/keys [id]}]
  {::pc/input #{::m.accounts/id}
   ::pc/output [{::m.accounts/currency [::m.currencies/id]}
                ::m.accounts/initial-value
                ::m.accounts/name
                {::m.accounts/user [::m.users/id]}]}
  (let [record (q.accounts/read-record id)
        id (:db/id record)]
    (assoc record ::m.accounts/id id)))

(defresolver accounts-resolver
  [_env _props]
  {::pc/output [{:all-accounts [::m.accounts/id]}]}
  {:all-accounts
   (map (fn [id] [::m.accounts/id id]) (q.accounts/index-ids))})

(defresolver account-link-resolver
  [_env {::m.accounts/keys [id]}]
  {::pc/input #{::m.accounts/id}
   ::pc/output [{::m.accounts/link [::m.accounts/id]}]}
  {::m.accounts/link [[::m.accounts/id id]]})

(defresolver user-account-resolver
  [_env {::m.accounts/keys [id]}]
  {::pc/input #{::m.users/id}
   ::pc/output [{::m.accounts/currency [::m.currencies/id]}
                ::m.accounts/initial-value
                ::m.accounts/name
                {::m.accounts/user [::m.users/id]}]}
  (get sample/account-map id))

(defresolver user-accounts-resolver
  [_env {::m.users/keys [id]}]
  {::pc/input #{::m.users/id}
   ::pc/output [{::m.users/accounts [::m.accounts/id]}]}
  {::m.users/accounts
   (->> (vals sample/account-map)
        (filter (fn [account] (= id (get-in account [::m.accounts/user ::m.users/id]))))
        (map (fn [{::m.accounts/keys [id]}] [::m.accounts/id id])))})

(def resolvers
  [account-resolver
   account-link-resolver
   accounts-resolver
   user-accounts-resolver])

(comment
  (q.accounts/index-records)

  (require 'dinsro.mocks)
  (dinsro.mocks/mock-account))
