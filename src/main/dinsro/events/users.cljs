(ns dinsro.events.users
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.events.utils :as eu :include-macros true]
   [dinsro.events.utils.impl]
   [dinsro.model.users :as m.users]
   [taoensso.timbre :as timbre]))

(s/def ::item ::m.users/item)

(eu/declare-model 'dinsro.events.users)
(eu/declare-fetch-index-method 'dinsro.events.users)
(eu/declare-fetch-record-method 'dinsro.events.users)
(eu/declare-delete-record-method 'dinsro.events.users)

(defn init-handlers!
  [store]
  (doto store
    (eu/register-model-store 'dinsro.events.users)
    (eu/register-fetch-index-method 'dinsro.events.users [:api-index-users])
    (eu/register-fetch-record-method 'dinsro.events.users [:api-show-user])
    (eu/register-delete-record-method 'dinsro.events.users [:api-show-user]))
  store)
