(ns dinsro.mutations.ui
  (:require
   [com.fulcrologic.fulcro.mutations :refer [defmutation]]))

(defmutation reset-global-error [_]
  (action [{:keys [state]}]
    (swap! state dissoc :ui/global-error))
  (refresh [_]
           [:ui/global-error]))
