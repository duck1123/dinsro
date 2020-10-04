(ns dinsro.event-utils
  (:require
   [clojure.spec.alpha :as s]
   #?(:cljs [dinsro.events :as e])
   [dinsro.store :as st]
   [ring.util.http-status :as status]
   [taoensso.timbre :as timbre]))

(s/def ::ns-sym symbol)

(defn inspect-registry
  []
  #?(:cljs
     (.log js/console
           (sort
            (fn [a# b#] (compare (str a#) (str b#)))
            (keys (s/registry))))))

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

#?(:cljs
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
       [(keyword ns-sym "do-fetch-index-failed")])}))

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

#?(:cljs
   (defn do-fetch-record
     [ns-sym path-selector store {:keys [db]} [id success failure]]
     {:db (assoc db (keyword ns-sym "do-fetch-record-state") :loading)
      :http-xhrio
      (e/fetch-request-auth
       (conj path-selector {:id id})
       store
       (:token db)
       (or success [(keyword ns-sym "do-fetch-record-success")])
       (or failure [(keyword ns-sym "do-fetch-record-failed")]))}))

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

#?(:cljs
   (defn do-delete-record
     [ns-sym path-selector store {:keys [db]} [item]]
     {:http-xhrio
      (e/delete-request-auth
       (conj path-selector {:id (:db/id item)})
       store
       (:token db)
       [(keyword ns-sym "do-delete-record-success")]
       [(keyword ns-sym "do-delete-record-failed")])}))

;; Macros

(defmacro declare-fetch-index-method
  [ns-sym]
  `(do
     #_(taoensso.timbre/infof "declaring fetch index method - %s" ~ns-sym)
     (let [fetch-kw# (keyword ~ns-sym "do-fetch-index-state")
           do-fetch-index-cofx-kw# (keyword ~ns-sym "do-fetch-index-cofx")
           do-fetch-index-event-kw# (keyword ~ns-sym "do-fetch-index-event")
           do-fetch-index-response-kw# (keyword ~ns-sym "do-fetch-index-response")]
       (clojure.spec.alpha/def-impl fetch-kw#
         :dinsro.spec/state
         :dinsro.spec/state)

       (clojure.spec.alpha/def-impl do-fetch-index-cofx-kw#
         (s/keys)
         (s/keys))

       (clojure.spec.alpha/def-impl do-fetch-index-event-kw#
         (s/keys)
         (s/keys))

       (clojure.spec.alpha/def-impl (keyword ~ns-sym "do-fetch-index-response")
         (s/keys)
         (s/keys)))))

(defmacro declare-fetch-record-method
  [ns-sym]
  `(do
     #_(taoensso.timbre/infof "declaring fetch record - %s" ~ns-sym)
     (let [fetch-kw# (keyword ~ns-sym "do-fetch-record-state")
           do-fetch-record-failed-cofx-kw# (keyword ~ns-sym "do-fetch-record-failed-cofx")
           do-fetch-record-failed-event-kw# (keyword ~ns-sym "do-fetch-record-failed-event")
           do-fetch-record-failed-response-kw# (keyword ~ns-sym "do-fetch-record-failed-response")]

       (clojure.spec.alpha/def-impl fetch-kw#
         :dinsro.spec/state
         :dinsro.spec/state)

       (clojure.spec.alpha/def-impl do-fetch-record-failed-cofx-kw#
         (s/keys)
         (s/keys))

       (clojure.spec.alpha/def-impl do-fetch-record-failed-event-kw#
         (s/keys)
         (s/keys))

       (clojure.spec.alpha/def-impl do-fetch-record-failed-response-kw#
         (s/keys)
         (s/keys)))))

(defmacro declare-delete-record-method
  [ns-sym]
  `(do
     #_(taoensso.timbre/infof "declaring delete record - %s" ~ns-sym)))

(defmacro declare-model
  [ns-sym]
  `(let [item-key# (keyword ~ns-sym "item")
         items-key# (keyword ~ns-sym "items")
         item-map-key# (keyword ~ns-sym "items")]
     #_(timbre/infof "Declaring model - %s" ~ns-sym)
     (clojure.spec.alpha/def-impl item-map-key#
       (clojure.spec.alpha/map-of :dinsro.spec/id item-key#)
       (clojure.spec.alpha/map-of :dinsro.spec/id item-key#))
     (clojure.spec.alpha/def-impl items-key#
       (clojure.spec.alpha/coll-of item-key#)
       (clojure.spec.alpha/coll-of item-key#))))

(defmacro declare-form
  [ns-sym
   form-data-spec
   form-defs]
  `(do
     #_(taoensso.timbre/infof "declaring form - %s" ~ns-sym)
     (clojure.spec.alpha/def-impl
       (keyword ~ns-sym "shown?")
       boolean?
       boolean?)

     (clojure.spec.alpha/def-impl
       (keyword ~ns-sym "form-data")
       ~form-data-spec
       ~form-data-spec)

     (def ~'form-defs ~form-defs)))

(defmacro declare-subform
  [ns-sym form-keys]
  `(do
     #_(taoensso.timbre/infof "declaring form - %s" ~ns-sym)

     (clojure.spec.alpha/def-impl
       (keyword ~ns-sym "shown?")
       boolean?
       boolean?)

     (clojure.spec.alpha/def-impl
       (keyword ~ns-sym "form-data")
       (clojure.spec.alpha/keys)
       (clojure.spec.alpha/keys))))

(defmacro register-fetch-index-method
  [store ns-sym path-selector]
  `(do
     #_(taoensso.timbre/infof "registering index method - %s" ~ns-sym)
     (doto ~store
       (st/reg-basic-sub
        (keyword ~ns-sym "do-fetch-index-state"))
       (st/reg-event-fx
        (keyword ~ns-sym "do-fetch-index-success")
        (partial do-fetch-index-success ~ns-sym))
       (st/reg-event-fx
        (keyword ~ns-sym "do-fetch-index-failed")
        (partial do-fetch-index-failed ~ns-sym))
       (st/reg-event-fx
        (keyword ~ns-sym "do-fetch-index")
        (partial do-fetch-index ~ns-sym ~path-selector ~store)))))

(defmacro register-fetch-record-method
  [store ns-sym path-selector]
  `(do
     #_(timbre/infof "Registering fetch method - %s" ~ns-sym)
     (doto ~store
       (dinsro.store/reg-basic-sub
        (keyword ~ns-sym "do-fetch-record-state"))
       (dinsro.store/reg-event-fx
        (keyword ~ns-sym "do-fetch-record-success")
        (partial do-fetch-record-success ~ns-sym))
       (dinsro.store/reg-event-fx
        (keyword ~ns-sym "do-fetch-record-failed")
        (partial do-fetch-record-failed ~ns-sym))
       (dinsro.store/reg-event-fx
        (keyword ~ns-sym "do-fetch-record")
        (partial do-fetch-record ~ns-sym ~path-selector ~store)))))

(defmacro register-delete-record-method
  [store ns-sym path-selector]
  `(do
     #_(timbre/infof "Registering delete method - %s" ~ns-sym)
     (doto ~store
       (dinsro.store/reg-basic-sub
        (keyword ~ns-sym "do-delete-record-state"))
       (dinsro.store/reg-event-fx
        (keyword ~ns-sym "do-delete-record-success")
        (partial do-delete-record-success ~ns-sym))
       (dinsro.store/reg-event-fx
        (keyword ~ns-sym "do-delete-record-failed")
        (partial do-delete-record-failed ~ns-sym))
       (dinsro.store/reg-event-fx
        (keyword ~ns-sym "do-delete-record")
        (partial do-delete-record ~ns-sym ~path-selector ~store)))))

(defmacro register-model-store
  [store ns-sym]
  `(doto ~store
     (dinsro.store/reg-basic-sub (keyword ~ns-sym "item-map"))
     (dinsro.store/reg-sub (keyword ~ns-sym "item") (partial item-sub ~ns-sym))
     (dinsro.store/reg-sub (keyword ~ns-sym "items") (partial items-sub ~ns-sym))))

(defmacro register-form
  [store ns-sym]
  `(do
     #_(timbre/infof "Registering form - %s" ~ns-sym)
     (doto ~store
       (dinsro.store/reg-basic-sub (keyword ~ns-sym "shown?"))
       (dinsro.store/reg-set-event (keyword ~ns-sym "shown?")))

     (doseq [[out-key# in-key# default#] ~'form-defs]
       #_(timbre/infof "Registering key - %s" in-key#)
       (doto ~store
         (dinsro.store/reg-basic-sub in-key#)
         (dinsro.store/reg-set-event in-key#)))))

(defmacro register-subform
  [store ns-sym]
  `(do
     #_(timbre/infof "Registering sub form - %s" ~ns-sym)
     (doto ~store
       (dinsro.store/reg-basic-sub (keyword ~ns-sym "shown?"))
       (dinsro.store/reg-set-event (keyword ~ns-sym "shown?")))))
