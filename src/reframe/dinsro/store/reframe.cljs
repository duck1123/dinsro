(ns dinsro.store.reframe
  (:require
   [dinsro.store :refer [Store]]
   [kee-frame.core :as kf]
   [re-frame.core :as rf]
   [reframe-utils.core :as rfu]
   [taoensso.timbre :as timbre]))

(deftype ReFrameStore []
  Store

  (get-state [_] nil)

  (subscribe [_ selector]
    ;; (timbre/infof "reframe sub - %s" selector)
    (or (rf/subscribe selector)
        (throw (ex-info (str "No handler - " selector) {:selector selector}))))

  (dispatch [_ selector]
    ;; (timbre/infof "reframe dispatch - %s" selector)
    (rf/dispatch selector))

  (path-for [store handler]
    (kf/path-for handler))

  (path-for [store handler params]
    (kf/path-for handler params))

  (reg-basic-sub [this key]
    (rfu/reg-basic-sub key))

  (reg-basic-sub
    [store name k]
    (rfu/reg-basic-sub name k))

  (reg-basic-sub [store name k sort-fn]
    (rfu/reg-basic-sub name k sort-fn))

  (reg-event-fx [this key handler]
    (kf/reg-event-fx key handler))

  (reg-event-fx [this key interceptors handler]
    (kf/reg-event-fx key interceptors handler))

  (reg-set-event [this key]
    (rfu/reg-set-event key))

  (reg-sub [_ selector handler]
    (rf/reg-sub selector handler)))

(defn reframe-store
  []
  (->ReFrameStore))
