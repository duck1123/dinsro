(ns dinsro.components.show-currency
  (:require [dinsro.components.buttons :as c.buttons]
            [dinsro.components.debug :as c.debug]
            [dinsro.events.debug :as e.debug]
            [dinsro.spec.currencies :as s.currencies]
            [dinsro.translations :refer [tr]]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]))

(defn-spec show-currency vector?
  [currency ::s.currencies/item]
  (let [currency-id (:db/id currency)
        name (::s.currencies/name currency)]
    [:<>
     [c.debug/debug-box currency]
     [:p (tr [:name-label]) name]
     (when @(rf/subscribe [::e.debug/shown?])
       [c.buttons/delete-currency currency])]))
