(ns dinsro.mutations
  (:require
   [com.wsscode.pathom.connect :as pc :refer [defmutation]]
   [dinsro.actions.accounts :as a.accounts]
   [dinsro.actions.categories :as a.categories]
   [dinsro.actions.currencies :as a.currencies]
   [dinsro.actions.rate-sources :as a.rate-sources]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.users :as m.users]
   [dinsro.queries.accounts :as q.accounts]
   [dinsro.queries.categories :as q.categories]
   [dinsro.queries.currencies :as q.currencies]
   [dinsro.queries.rate-sources :as q.rate-sources]
   [dinsro.queries.users :as q.users]
   [taoensso.timbre :as timbre]))

(defmutation create-account
  [{{{:keys [identity]} :session} :request} params]
  {::pc/params #{::m.accounts/name}
   ::pc/output [:status
                :items [::m.accounts/id]]}
  (if-let [user-id (q.users/find-id-by-email identity)]
    (let [currency-id (get-in params [::m.accounts/currency :db/id])
          params      (assoc-in params [::m.accounts/user :db/id] user-id)
          params      (if (zero? currency-id)
                        (dissoc params ::m.accounts/currency)
                        params)]
      (if-let [record (a.accounts/create! params)]
        {:status :success
         :items  [{::m.accounts/id (:db/id record)}]}
        {:status :failure}))
    {:status :no-user}))

(defmutation create-category
  [{{{:keys [identity]} :session} :request} {::m.categories/keys [name]}]
  {::pc/params #{::m.categories/name}
   ::pc/output [:status
                :created-category [::m.categories/id]]}
  (if-let [user-id (q.users/find-id-by-email identity)]
    (let [params {::m.categories/name name
                  :user-id            user-id}]
      (if-let [record (a.categories/create! params)]
        {:status           :success
         :created-category [{::m.categories/id (:db/id record)}]}
        {:status :failure}))
    {:status :no-user}))

(defmutation create-currency
  [{{{:keys [identity]} :session} :request} {::m.currencies/keys [name]}]
  {::pc/params #{::m.currencies/name}
   ::pc/output [:status
                :created-category [::m.categories/id]]}
  (if-let [user-id (q.users/find-id-by-email identity)]
    (let [params {::m.currencies/name name
                  :user-id            user-id}]
      (if-let [record (a.currencies/create! params)]
        {:status           :success
         :created-category [{::m.currencies/id (:db/id record)}]}
        {:status :failure}))
    {:status :no-user}))

(defmutation create-rate-source
  [_env params]
  {::pc/params #{:name :url :currency-id}
   ::pc/output [:status
                {:item [::m.rate-sources/id]}]}
  (if-let [record (a.rate-sources/create! params)]
    {:status :success
     :item   [{::m.rate-sources/id (:db/id record)}]}
    {:status :failure}))

(defmutation delete-account
  [_env {::m.accounts/keys [id]}]
  {::pc/params #{::m.accounts/id}
   ::pc/output [:status]}
  (q.accounts/delete-record id)
  {:status :success})

(defmutation delete-category
  [_env {::m.categories/keys [id]}]
  {::pc/params #{::m.categories/id}
   ::pc/output [:status]}
  (q.categories/delete-record id)
  {:status :success})

(defmutation delete-currency
  [_request {::m.currencies/keys [id]}]
  {::pc/params #{::m.currencies/id}
   ::pc/output [:status :message]}
  (if (zero? id)
    {:status  :failure
     :message "Bitcoin cannot be killed"}
    (do
      (q.currencies/delete-record id)
      {:status :success})))

(defmutation delete-rate-source
  [_request {::m.rate-sources/keys [id]}]
  {::pc/params #{::m.rate-sources/id}
   ::pc/output [:status]}
  (q.rate-sources/delete-record id)
  {:status :success})

(defmutation delete-user
  [_request {::m.users/keys [id]}]
  {::pc/params #{::m.users/id}
   ::pc/output [:status]}
  (q.users/delete-record id)
  {:status :success})

(def mutations
  [create-account
   create-category
   create-currency
   create-rate-source
   delete-account
   delete-category
   delete-currency
   delete-rate-source
   delete-user])
