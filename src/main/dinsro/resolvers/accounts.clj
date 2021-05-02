(ns dinsro.resolvers.accounts
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [com.wsscode.pathom.connect :as pc :refer [defresolver]]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.users :as m.users]
   [dinsro.queries.accounts :as q.accounts]
   [dinsro.queries.currencies :as q.currencies]
   [dinsro.queries.users :as q.users]
   [dinsro.sample :as sample]
   [taoensso.timbre :as timbre]))

(>defn resolve-account
  [id]
  [::m.accounts/id => ::m.accounts/item]
  (let [record        (q.accounts/read-record id)
        currency-id   (get-in record [::m.accounts/currency :db/id])
        currency      (q.currencies/read-record currency-id)
        currency-name (::m.currencies/name currency)
        user-eid      (get-in record [::m.accounts/user :db/id])]
    (-> record
        (assoc ::m.accounts/id id)
        (assoc ::m.accounts/user [(m.users/ident user-eid)])
        (assoc ::m.accounts/currency [(m.currencies/ident currency-name)]))))

(defn resolve-account-link
  [id]
  {::m.accounts/link [(m.accounts/ident id)]})

(>defn resolve-accounts
  []
  [=> (s/keys)]
  (let [records (q.accounts/index-records)]
    {:all-accounts (map m.accounts/ident-item records)}))

(defn resolve-user-account
  [id]
  (get sample/account-map id))

(>defn resolve-user-accounts
  [user-id]
  [::m.users/id => (s/keys)]
  (let [eid      (q.users/find-eid-by-id user-id)
        records  (q.accounts/index-records-by-user eid)
        accounts (map
                  (fn [{{:db/keys [id]} ::m.accounts/user}]
                    (m.accounts/ident id))
                  records)]
    {::m.users/accounts accounts}))

(defresolver account-resolver
  [_env {::m.accounts/keys [id]}]
  {::pc/input  #{::m.accounts/id}
   ::pc/output [{::m.accounts/currency [::m.currencies/name]}
                ::m.accounts/initial-value
                ::m.accounts/name
                {::m.accounts/user [::m.users/username]}]}
  (resolve-account id))

(defresolver accounts-resolver
  [_env _props]
  {::pc/output [{:all-accounts [::m.accounts/id]}]}
  (resolve-accounts))

(defresolver account-link-resolver
  [_env {::m.accounts/keys [id]}]
  {::pc/input  #{::m.accounts/id}
   ::pc/output [{::m.accounts/link [::m.accounts/id]}]}
  (resolve-account-link id))

(defresolver currency-account-resolver
  [_env {::m.currencies/keys [id]}]
  {::pc/input  #{::m.currencies/id}
   ::pc/output [{::m.accounts/currency [::m.currencies/id]}
                ::m.accounts/initial-value
                ::m.accounts/name
                {::m.accounts/user [::m.users/id]}]}
  (get sample/account-map id))

(defresolver user-account-resolver
  [_env {::m.accounts/keys [id]}]
  {::pc/input  #{::m.users/id}
   ::pc/output [{::m.accounts/currency [::m.currencies/id]}
                ::m.accounts/initial-value
                ::m.accounts/name
                {::m.accounts/user [::m.users/id]}]}
  (resolve-user-account id))

(defresolver user-accounts-resolver
  [_env {::m.users/keys [id]}]
  {::pc/input  #{::m.users/id}
   ::pc/output [{::m.users/accounts [::m.accounts/id]}]}
  (resolve-user-accounts id))

(def resolvers
  [account-resolver
   account-link-resolver
   accounts-resolver
   user-accounts-resolver])

(comment
  (q.accounts/index-records)

  (require 'dinsro.mocks)
  (dinsro.mocks/mock-account))
