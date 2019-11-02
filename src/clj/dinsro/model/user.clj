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

(defn create-user!
  [user-params]
  (d/transact db/*conn* {:tx-data [{:user/id 1 :user/email "duck@kronkltd.net"}]})
  #_(if-let [user (prepare-user user-params)]
    (merge user (db/create-user! user))))

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
  (if-let [uid (ffirst
             (d/q
              {:query '[:find ?e
                        :in $ ?user-id
                        :where [?e :user/id
                                ?user-id
                                ]]
               :args [@db/*conn* user-id]}))]
    (d/pull @db/*conn* '[*] uid))

  #_(db/read-user {:id user-id}))
