(ns dinsro.events.users
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.event-utils :as eu :include-macros true]
   [dinsro.events :as e]
   [dinsro.spec.users :as s.users]
   [taoensso.timbre :as timbre]))

(s/def ::item ::s.users/item)

(eu/declare-model 'dinsro.events.users)
(eu/declare-fetch-index-method 'dinsro.events.users)
(eu/declare-fetch-record-method 'dinsro.events.users)

;; Delete

(defn do-delete-record-success
  [_store _cofx _event]
  {:dispatch [::do-fetch-index]})

(defn do-delete-record-failed
  [_store {:keys [db]} [{:keys [id]}]]
  {:db (-> db
           (assoc :failed true)
           (assoc :delete-record-failure-id id))})

(defn do-delete-record
  [store {:keys [db]} [user]]
  {:http-xhrio
   (e/delete-request-auth
    [:api-show-user {:id (:db/id user)}]
    store
    (:token db)
    [::do-delete-record-success]
    [::do-delete-record-failed])})

;; Index

(s/def ::do-fetch-index-state keyword?)

(defn init-handlers!
  [store]
  (doto store
    (eu/register-model-store 'dinsro.events.users)
    (eu/register-fetch-index-method 'dinsro.events.users [:api-index-users])
    (eu/register-fetch-record-method 'dinsro.events.users [:api-show-user])
    (eu/register-delete-record-method 'dinsro.events.users [:api-show-user]))
  store)
