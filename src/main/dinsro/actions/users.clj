(ns dinsro.actions.users
  (:require
   [buddy.hashers :as hashers]
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [dinsro.model.users :as m.users]
   [expound.alpha :as expound]
   [taoensso.timbre :as log]))

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
        (log/warnf "not valid: %s" (expound/expound-str ::m.users/params params))
        nil))))
