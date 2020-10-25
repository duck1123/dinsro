(ns dinsro.events.currencies
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.event-utils :as eu :include-macros true]
   [dinsro.events :as e]
   [dinsro.spec.currencies :as s.currencies]
   [dinsro.store :as st]
   [taoensso.timbre :as timbre]))

(def items-sub-default [])

(s/def ::item ::s.currencies/item)

(eu/declare-model 'dinsro.events.currencies)
(eu/declare-fetch-index-method 'dinsro.events.currencies)
(eu/declare-fetch-record-method 'dinsro.events.currencies)
(eu/declare-delete-record-method 'dinsro.events.currencies)

;; Create

(defn do-submit-success
  [_store _cofx _event]
  {:dispatch [::do-fetch-index]})

(defn do-submit-failed
  [_store _cofx _event]
  {})

(defn do-submit
  [store {:keys [db]} [data]]
  {:http-xhrio
   (e/post-request-auth
    [:api-index-currencies]
    store
    (:token db)
    [::do-submit-success]
    [::do-submit-failed]
    data)})

(defn init-handlers!
  [store]
  (doto store
    (eu/register-model-store 'dinsro.events.currencies)
    (eu/register-fetch-index-method 'dinsro.events.currencies [:api-index-currencies])
    (eu/register-fetch-record-method 'dinsro.events.currencies [:api-show-currency])
    (eu/register-delete-record-method 'dinsro.events.currencies [:api-delete-currency])

    (st/reg-event-fx ::do-submit-success (partial do-submit-success store))
    (st/reg-event-fx ::do-submit-failed (partial do-submit-failed store))
    (st/reg-event-fx ::do-submit (partial do-submit store)))
  store)
