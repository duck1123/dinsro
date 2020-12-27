(ns dinsro.resolvers.accounts
  (:require
   [com.wsscode.pathom.connect :as pc :refer [defresolver]]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.users :as m.users]
   [dinsro.sample :as sample]
   [taoensso.timbre :as timbre]))


(defresolver account-resolver
  [_env {::m.accounts/keys [id]}]
  {::pc/input #{::m.accounts/id}
   ::pc/output [{::m.accounts/currency [::m.currencies/id]}
                ::m.accounts/initial-value
                ::m.accounts/name
                {::m.accounts/user [::m.users/id]}]}
  (get sample/account-map id))

(defresolver account-map-resolver
  [_env _props]
  {::pc/output [::m.accounts/map]}
  {::m.accounts/map sample/account-map})

(defresolver accounts-resolver
  [_env _props]
  {::pc/output [{:all-accounts [::m.accounts/id]}]}
  {:all-accounts (map (fn [id] [::m.accounts/id id])
                      (keys sample/account-map))})

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

(def resolvers [account-resolver accounts-resolver account-map-resolver user-accounts-resolver])
