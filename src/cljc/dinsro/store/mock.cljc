(ns dinsro.store.mock
  (:require
   [dinsro.store :refer [Store]]
   [re-frame-lib.core :as rfl]
   [taoensso.timbre :as timbre]))

(deftype MockStore [state]
  Store

  (get-state [_] state)

  (subscribe [_ selector]
    (timbre/infof "reframe sub - %s" selector)
    (or (rfl/subscribe state selector)
        (throw (ex-info (str "No handler - " selector) {:selector selector}))))

  (dispatch [_ selector]
    (timbre/infof "reframe dispatch - %s" selector)
    (rfl/dispatch state selector))

  (reg-sub [_ selector handler]
    (rfl/reg-sub state selector handler)))

(defn mock-store
  []
  (let [state (rfl/new-state)]
    (->MockStore state)))
