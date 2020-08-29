(ns dinsro.store.reframe
  (:require
   [dinsro.store :refer [Store]]
   [re-frame.core :as rf]
   [taoensso.timbre :as timbre]))

(deftype ReFrameStore []
  Store

  (get-state [_] nil)

  (subscribe [_ selector]
    (timbre/infof "reframe sub - %s" selector)
    (or (rf/subscribe selector)
        (throw (ex-info (str "No handler - " selector) {:selector selector}))))

  (dispatch [_ selector]
    (timbre/infof "reframe dispatch - %s" selector)
    (rf/dispatch selector))

  (reg-sub [_ selector handler]
    (rf/reg-sub selector handler)))

(defn reframe-store
  []
  (->ReFrameStore))
