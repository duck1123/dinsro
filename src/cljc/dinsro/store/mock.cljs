(ns dinsro.store.mock
  (:require
   [dinsro.store :refer [Store]]
   [re-frame.core :as rf]
   [taoensso.timbre :as timbre]))

(deftype MockStore []
  Store

  (get-state [_this] {})

  (subscribe [_this key]
    (timbre/infof "reframe sub - %s" key)
    (or (rf/subscribe key)
        (throw (ex-info (str "No handler - " key) {:key key}))))

  (dispatch [_ a]
    (timbre/infof "reframe dispatch - %s" a)
    (rf/dispatch a)))

(defn mock-store
  []
  (->MockStore))
