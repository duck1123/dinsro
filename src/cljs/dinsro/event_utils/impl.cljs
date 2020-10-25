(ns dinsro.event-utils.impl
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.events :as e]
   [ring.util.http-status :as status]
   [taoensso.timbre :as timbre]))

(s/def ::ns-sym symbol)

(defn inspect-registry
  []
  (.log js/console
     (sort
      (fn [a# b#] (compare (str a#) (str b#)))
      (keys (s/registry)))))

(defn item-sub
  [ns-sym db [_ id]]
  (let [item-map (get db (keyword ns-sym "item-map"))]
    (get item-map id)))

(defn items-sub
  "Subscription handler: Index all items"
  [ns-sym db _event ]
  #_(timbre/debugf "Getting items: %s" ns-sym)
  (let [item-map (get db (keyword ns-sym "item-map"))]
    (sort-by :db/id (vals item-map))))

(s/fdef items-sub
  :args (s/cat :ns-sym symbol?
               :db (s/keys)
               :event (s/cat :kw keyword?)))

(defn do-fetch-index-success
  [ns-sym {:keys [db]} [{:keys [items]}]]
  {:db (-> db
           (update (keyword ns-sym "item-map") merge
                   (into {} (map #(vector (:db/id %) %) items)))
           (assoc (keyword ns-sym "do-fetch-index-state") :loaded))})

(s/def ::do-fetch-index-success-cofx (s/keys))
(s/def ::do-fetch-index-success-event (s/cat :response (s/keys)))
(s/def ::do-fetch-index-success-request
  (s/cat :ns-sym ::ns-sym
         :cofx ::do-fetch-index-success-cofx
         :event ::do-fetch-index-success-event))
(s/def ::do-fetch-index-success-response (s/keys))

(s/fdef do-fetch-index-success
  :args ::do-fetch-index-success-request
  :ret ::do-fetch-index-success-response)

(defn do-fetch-index-failed
  [ns-sym {:keys [db]} _]
  {:db (assoc db (keyword ns-sym "do-fetch-index-state") :failed)})

(defn do-fetch-index
   [ns-sym path-selector store {:keys [db]} _]
   #_(timbre/debugf "Fetching index - %s - %s" ns-sym path-selector)
   {:db (assoc db (keyword ns-sym "do-fetch-index-state") :loading)
    :http-xhrio
    (e/fetch-request-auth
     path-selector
     store
     (:token db)
     [(keyword ns-sym "do-fetch-index-success")]
     [(keyword ns-sym "do-fetch-index-failed")])})

(defn do-fetch-record-success
  [ns-sym {:keys [db]} [{:keys [item]}]]
  {:db (-> db
           (assoc (keyword ns-sym "do-fetch-record-state") :loaded)
           (assoc (keyword ns-sym "item") item)
           (assoc-in [(keyword ns-sym "item-map") (:db/id item)] item))})

(defn do-fetch-record-unauthorized
  [_ns-sym {:keys [db]} _event]
  (let [match (:kee-frame/route db)]
    {:db (assoc db :return-to match)
     :navigate-to [:login-page]}))

(defn do-fetch-record-failed
  [ns-sym {:keys [db]} [{:keys [status] :as request}]]
  (if (= status/forbidden status)
    {:dispatch [(keyword ns-sym "do-fetch-record-unauthorized") request]}
    {:db (assoc db (keyword ns-sym "do-fetch-record-state") :failed)}))

(defn do-fetch-record
   [ns-sym path-selector store {:keys [db]} [id success failure]]
   {:db (assoc db (keyword ns-sym "do-fetch-record-state") :loading)
    :http-xhrio
    (e/fetch-request-auth
     (conj path-selector {:id id})
     store
     (:token db)
     (or success [(keyword ns-sym "do-fetch-record-success")])
     (or failure [(keyword ns-sym "do-fetch-record-failed")]))})

(defn do-delete-record-success
  [ns-sym {:keys [db]} [{:keys [id]}]]
  (timbre/debugf "delete success - %s - %s" ns-sym id)
  {:db (update db (keyword ns-sym "item-map") #(dissoc % id))
   :dispatch [(keyword ns-sym "do-fetch-index") id]})


(defn do-delete-record-failed
  [ns-sym {:keys [db]} [{:keys [id]}]]
  {:db (-> db
           (assoc (keyword ns-sym "do-fetch-record-failed") true)
           (assoc (keyword ns-sym "do-fetch-record-failure-id") id))})

(defn do-delete-record
   [ns-sym path-selector store {:keys [db]} [item]]
   {:http-xhrio
    (e/delete-request-auth
     (conj path-selector {:id (:db/id item)})
     store
     (:token db)
     [(keyword ns-sym "do-delete-record-success")]
     [(keyword ns-sym "do-delete-record-failed")])})
