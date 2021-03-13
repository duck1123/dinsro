(ns dinsro.events.admin-accounts
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.events :as e]
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.utils.impl :as eui]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.specs :as ds]
   [dinsro.specs.events.accounts :as s.e.accounts]
   [dinsro.store :as st]
   [taoensso.timbre :as timbre]))

(s/def ::item ::m.accounts/item)

(s/def ::items (s/coll-of ::item))

(s/def ::item-sub-db (s/keys))
(s/def ::item-sub-event (s/cat :kw keyword? :id :db/id))
(s/def ::item-sub-request (s/cat :db ::item-sub-db
                                 :event ::item-sub-event))
(s/def ::item-sub-response (s/nilable ::item))

(defn item-sub
  [db event]
  (eui/item-sub 'dinsro.events.accounts db event))

(s/fdef item-sub
  :args ::item-sub-request
  :ret ::item-sub-response)

(defn items-sub
  "Subscription handler: Index all items"
  [db _]
  (map #(get-in db [::e.accounts/item-map %])
       (::item-ids db)))

(s/fdef items-sub
  :args (s/cat :item-map ::item-map
               :event (s/cat :kw keyword?))
  :ret ::items)

;; Index

(s/def ::do-fetch-index-state ::ds/state)
(def do-fetch-index-state ::do-fetch-index-state)

(defn do-fetch-index-success
  [{:keys [db]} [{:keys [items]}]]
  {:db (-> db
           (update ::e.accounts/item-map merge (into {} (map #(vector (:db/id %) %) items)))
           (assoc ::item-ids (map :db/id items))
           (assoc ::do-fetch-index-state :loaded))})

(defn do-fetch-index-failed
  [{:keys [db]} _]
  {:db (assoc db ::do-fetch-index-state :failed)})

(s/fdef do-fetch-index-failed
  :args (s/cat :cofx ::s.e.accounts/do-fetch-index-failed-cofx
               :event ::s.e.accounts/do-fetch-index-failed-event)
  :ret ::s.e.accounts/do-fetch-index-failed-response)

(defn do-fetch-index
  [store {:keys [db]} _]
  {:db (assoc db ::do-fetch-index-state :loading)
   :http-xhrio
   (e/fetch-request-auth
    [:api-admin-index-accounts]
    store
    (:token db)
    [::do-fetch-index-success]
    [::do-fetch-index-failed])})

(s/fdef do-fetch-index
  :args (s/cat :cofx ::s.e.accounts/do-fetch-index-cofx
               :event ::s.e.accounts/do-fetch-index-event)
  :ret ::s.e.accounts/do-fetch-index-response)

(defn init-handlers!
  [store]
  (doto store
    (st/reg-sub ::items items-sub)
    (st/reg-basic-sub ::do-fetch-index-state)
    (st/reg-event-fx ::do-fetch-index-success do-fetch-index-success)
    (st/reg-event-fx ::do-fetch-index-failed do-fetch-index-failed)
    (st/reg-event-fx ::do-fetch-index (partial do-fetch-index store)))
  store)
