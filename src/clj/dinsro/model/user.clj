(ns dinsro.model.user
  (:require [buddy.hashers :as hashers]
            [clojure.spec.alpha :as s]
            [datahike.api :as d]
            [dinsro.db.core :as db]
            [dinsro.specs :as ds]
            [orchestra.core :refer [defn-spec]]
            [taoensso.timbre :as timbre]))

(def schema
  [{:db/ident       :user/id
    :db/valueType   :db.type/long
    :db/cardinality :db.cardinality/one}
   {:db/ident       :user/name
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident       :user/password-hash
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident       :user/email
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}])

(defn init-schema
  []
  (d/transact db/*conn* schema))

;; (defn-spec prepare-user
;;   [registration-data ::ds/register-request]
;;   (let [{:keys [password]} registration-data]
;;     (if password
;;       (merge {:password-hash (hashers/derive password)}
;;              registration-data)
;;       nil)))

;; The return spec comes after the fn name.
(defn-spec my-inc integer?
  [a integer?] ; Each argument is followed by its spec.
  (+ a "a"))


(defn prepare-user
  [registration-data]
  (let [{:keys [password]} registration-data]
    (if password
      (merge {:password-hash (hashers/derive password)}
             registration-data)
      nil)))

(defn-spec create-user! ::ds/id
  [user-params ::ds/register-request]
  (let [response (d/transact db/*conn* {:tx-data [user-params]})]
    (get-in response [:tempids :db/current-tx])))

(defn list-users
  []
  (map
   (fn [id] (d/pull @db/*conn* '[*] (first id)))
   (d/q '[:find ?e :where [?e :user/id _]] @db/*conn*)))

(defn-spec delete-user any?
  [user-id ::ds/id]
  (db/delete-user! {:id user-id}))

(defn-spec read-user ::ds/user
  [user-id ::ds/id]
  (if-let [query '[:find ?e
                   :in $ ?user-id
                   :where [?e :user/id ?user-id]]
           response (d/q query @db/*conn* user-id)
           uid (ffirst response)]
    (d/pull @db/*conn* '[*] uid)))
