(ns dinsro.actions.users
  (:require
   [buddy.hashers :as hashers]
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [dinsro.model.users :as m.users]
   [dinsro.queries.users :as q.users]
   [expound.alpha :as expound]
   [lambdaisland.glogc :as log]))

(def param-rename-map
  {:username ::m.users/id})

(>defn prepare-record
  [params]
  [::m.users/input-params => (? ::m.users/params)]
  (let [password-hash (some-> params ::m.users/password hashers/derive)
        params        (if (seq password-hash)
                        (assoc params ::m.users/password-hash password-hash)
                        params)]
    (if (s/valid? ::m.users/params params)
      params
      (do
        (log/warn :prepare-record/not-valid {:message (expound/expound-str ::m.users/params params)})
        nil))))

(defn set-role!
  [user-id role]
  (log/info :set-role!/starting {:user-id user-id :role role})
  (if-let [user (q.users/read-record user-id)]
    (do
      (log/finer :set-role/user-found {:user user})
      (q.users/update! user-id {::m.users/role role}))
    (do
      (log/error :set-role!/user-not-read {:user-id user-id})
      (throw (RuntimeException. "user not found")))))
