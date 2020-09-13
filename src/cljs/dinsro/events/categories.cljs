(ns dinsro.events.categories
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.event-utils :as eu :include-macros true]
   [dinsro.events :as e]
   [dinsro.spec.categories :as s.categories]
   [dinsro.store :as st]
   [taoensso.timbre :as timbre]))

(def example-category
  {:db/id 1
   ::s.categories/name "Foo"
   ::s.categories/user {:db/id 12}})

(s/def ::item ::s.categories/item)

(eu/declare-model 'dinsro.events.categories)
(eu/declare-fetch-index-method 'dinsro.events.categories)
(eu/declare-fetch-record-method 'dinsro.events.categories)
(eu/declare-delete-record-method 'dinsro.events.categories)

;; Items by User

(defn items-by-user
  [{:keys [::item-map]} event]
  (let [[_ id] event]
    (filter #(= id (get-in % [::s.categories/user :db/id])) (vals item-map))))

;; Create

(defn do-submit-success
  [_ _ _]
  {:dispatch [::do-fetch-index]})

(defn do-submit-failed
  [_ _ _]
  {})

(defn do-submit
  [store {:keys [db]} [data]]
  {:http-xhrio
   (e/post-request-auth
    [:api-index-categories]
    store
    (:token db)
    [::do-submit-success]
    [::do-submit-failed]
    data)})

(defn init-handlers!
  [store]
  (doto store
    (eu/register-model-store 'dinsro.events.categories)
    (eu/register-fetch-index-method 'dinsro.events.categories [:api-index-categories])
    (eu/register-fetch-record-method 'dinsro.events.currencies [:api-show-category])
    (eu/register-delete-record-method 'dinsro.events.currencies [:api-delete-category])

    (st/reg-sub ::items-by-user items-by-user)
    (st/reg-event-fx ::do-submit-success (partial do-submit-success store))
    (st/reg-event-fx ::do-submit-failed (partial do-submit-failed store))
    (st/reg-event-fx ::do-submit (partial do-submit store)))
  store)
