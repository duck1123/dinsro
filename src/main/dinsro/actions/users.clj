(ns dinsro.actions.users
  (:require
   [buddy.hashers :as hashers]
   [clojure.set :as set]
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [dinsro.model.users :as m.users]
   [dinsro.specs.actions.users :as s.a.users]
   [expound.alpha :as expound]
   [taoensso.timbre :as timbre]))

(def param-rename-map
  {:name     ::m.users/name
   :email    ::m.users/email
   :username ::m.users/username})

(>defn prepare-record
  [params]
  [::s.a.users/create-params => (? ::m.users/params)]
  (let [password-hash (some-> params ::m.users/password hashers/derive)
        params        (-> params
                          (set/rename-keys param-rename-map)
                          (select-keys (vals param-rename-map)))
        params        (if (seq password-hash)
                        (assoc params ::m.users/password-hash password-hash)
                        params)]
    (if (s/valid? ::m.users/params params)
      params
      (do
        (timbre/warnf "not valid: %s" (expound/expound-str ::m.users/params params))
        nil))))
