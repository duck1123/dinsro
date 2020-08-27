(ns dinsro.store.reframe
  (:require
   [dinsro.store :refer [Store]]
   [re-frame.core :as rf]
   [taoensso.timbre :as timbre]))

(deftype ReFrameStore []
  Store

  (get-state [_this] {})

  (subscribe [_this key]
    (timbre/infof "reframe sub - %s" key)
    (or (rf/subscribe key)
        (throw (ex-info (str "No handler - " key) {:key key}))))

  (dispatch [_ a]
    (timbre/infof "reframe dispatch - %s" a)
    (rf/dispatch a)))

(defn reframe-store
  []
  (->ReFrameStore))
