(ns dinsro.model.user
  (:require [buddy.hashers :as hashers]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [datahike.api :as d]
            [dinsro.db.core :as db]
            [dinsro.spec.users :as s.users]
            [orchestra.core :refer [defn-spec]]
            [taoensso.timbre :as timbre]))

(defn-spec prepare-user (s/nilable ::s.users/item)
  [params ::s.users/params]
  (when-let [password (::s.users/password params)]
    (-> {::s.users/password-hash (hashers/derive password)}
        (merge params)
        (dissoc ::s.users/password))))

(defn-spec create-user! :db/id
  [user-params ::s.users/params]
  (let [tempid (d/tempid "user-id")]
    (let [user (prepare-user (assoc params :db/id tempid))
          response (d/transact db/*conn* {:tx-data [user]})]
     (get-in response [:tempids tempid]))))

(defn-spec list-user-ids any?
  []
  (map first (d/q '[:find ?e :where [?e ::s.users/email _]] @db/*conn*)))

(defn list-users
  []
  (->> (list-user-ids)
       (d/pull-many @db/*conn* '[::s.users/name ::s.users/email :db/id])))

(defn-spec delete-user any?
  [user-id :db/id]
  (d/transact db/*conn* {:tx-data [[:db/retractEntity user-id]]}))

(defn-spec delete-all nil?
  []
  (doseq [id (list-user-ids)]
    (delete-user id)))

(defn-spec read-user (s/nilable ::s.users/item)
  [user-id :db/id]
  (d/pull @db/*conn* '[::s.users/name ::s.users/email ::s.users/password-hash] user-id))

(defn-spec find-by-email (s/nilable ::s.users/item)
  [email ::s.users/email]
  (let [query '[:find ?id
                :in $ ?email
                :where [?id ::s.users/email ?email]]]
    (read-user (ffirst (d/q query @db/*conn* email)))))

(defn-spec mock-user ::s.users/item
  []
  (let [params (gen/generate (s/gen ::s.users/params))
        id (create-user! params)]
    (read-user id)))

(comment
  (mock-user)
  (::s.users/email (first (list-users)))

  (find-by-email (::s.users/email (first (list-users))))
  )
