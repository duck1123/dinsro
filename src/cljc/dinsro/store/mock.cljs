(ns dinsro.store.mock
  (:require
   [dinsro.routing :as routing]
   [dinsro.store :as st]
   [kee-frame.core :as kf]
   [kee-frame.router :as kfr]
   [re-frame-lib.core :as rfl]
   [reitit.core :as reitit]
   [reframe-utils.core :as rfu]
   [taoensso.timbre :as timbre]))

(deftype MockStore [state]
  st/Store

  (get-state [_] state)

  (subscribe [_ selector]
    (timbre/debugf "sub - %s" selector)
    (or (rfl/subscribe state selector)
        (throw (ex-info (str "No handler - " selector) {:selector selector}))))

  (dispatch [_store selector]
    (timbre/debugf "dispatch - %s" selector)
    (rfl/dispatch state selector))

  (path-for [store handler]
    (st/path-for store handler nil))

  (path-for [store handler params]
    (let [routes (reitit/router routing/routes)]
      (kfr/assert-route-data handler)
      (or (kfr/match-data routes handler false)
          (kfr/url-not-found routes handler))))

  (reg-basic-sub
    [store k]
    (st/reg-basic-sub store k k))

  (reg-basic-sub
    [store name k]
    (let [handler (fn [db _] (get-in db (rfu/collify k)))]
      (st/reg-sub store name handler)))

  (reg-basic-sub [store name k sort-fn]
    (timbre/debugf "basic-sub - %s" key)
    (let [handler (fn [db _]
                    (->> k rfu/collify (get-in db) (sort-by sort-fn)))]
      (st/reg-sub store name handler)))

  (reg-event-fx [store id handler]
    (st/reg-event-fx store id nil handler))

  (reg-event-fx [store id interceptors handler]
    (timbre/debugf "reg-fx - %s" id)
    (rfl/reg-event-fx state id (concat kf/kee-frame-interceptors interceptors) handler))

  (reg-set-event [store k]
    (st/reg-set-event store (rfu/kw-prefix k "set-") k))

  (reg-set-event [store event-kw kw]
    (timbre/debugf "reg-set - %s" event-kw)
    (let [kw (rfu/collify kw)]
      (rfl/reg-event-db
       state
       event-kw
       (fn [db [_ v]] (assoc-in db kw v)))))

  (reg-sub [store selector handler]
    (timbre/debugf "reg-sub - %s" selector)
    (rfl/reg-sub state selector handler)))

(defn mock-store
  []
  (let [state (rfl/new-state)]
    (->MockStore state)))
