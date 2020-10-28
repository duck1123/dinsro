(ns dinsro.events.rate-sources
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.events :as e]
   [dinsro.events.utils :as eu :include-macros true]
   [dinsro.events.utils.impl :as eui]
   [dinsro.spec.rate-sources :as s.rate-sources]
   [dinsro.store :as st]
   [taoensso.timbre :as timbre]))

(s/def ::item ::s.rate-sources/item)

(eu/declare-model 'dinsro.events.rate-sources)
(eu/declare-fetch-index-method 'dinsro.events.rate-sources)
(eu/declare-fetch-record-method 'dinsro.events.rate-sources)
(eu/declare-delete-record-method 'dinsro.events.rate-sources)

;; Submit

(defn do-submit-success
  [_ _ _]
  {:dispatch [::do-fetch-index]})

(defn do-submit-failed
  [_ _ _]
  {:dispatch [::do-fetch-index]})

(defn do-submit
  [store {:keys [db]} [data]]
  {:http-xhrio
   (e/post-request-auth
    [:api-index-rate-sources]
    store
    (:token db)
    [::do-submit-success]
    [::do-submit-failed]
    data)})

(defn do-run-source-failed
  [_ _ _]
  {})

(defn do-run-source-success
  [_ _ _]
  {})

(defn do-run-source
  [store {:keys [db]} [id]]
  (timbre/infof "running: %s" id)
  {:http-xhrio
   (e/post-request-auth
    [:api-run-rate-source {:id id}]
    store
    (:token db)
    [::do-run-source-success]
    [::do-run-source-failed]
    {})})

(defn init-handlers!
  [store]
  (doto store
    (eu/register-model-store 'dinsro.events.rate-sources)
    (eu/register-fetch-index-method 'dinsro.events.rate-sources [:api-index-rate-sources])
    (eu/register-fetch-record-method 'dinsro.events.rate-sources [:api-show-rate-source])
    (eu/register-delete-record-method 'dinsro.events.rate-sources [:api-delete-rate-source])
    (st/reg-sub ::do-fetch-index-state (fn [db _] (get db ::do-fetch-index-state :invalid)))
    (st/reg-event-fx ::do-submit-failed (partial do-submit-failed store))
    (st/reg-event-fx ::do-submit-success (partial do-submit-success store))
    (st/reg-event-fx ::do-submit (partial do-submit store))
    (st/reg-event-fx ::do-run-source-failed (partial do-run-source-failed store))
    (st/reg-event-fx ::do-run-source-success (partial do-run-source-success store))
    (st/reg-event-fx ::do-run-source (partial do-run-source store)))
  store)
