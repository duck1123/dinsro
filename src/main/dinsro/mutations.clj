(ns dinsro.mutations
  (:require
   [com.wsscode.pathom.connect :as pc :refer [defmutation]]
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
    {:status :failure
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
  [delete-account
   delete-category
   delete-currency
   delete-rate-source
   delete-user])
