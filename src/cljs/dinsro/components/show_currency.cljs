(ns dinsro.components.show-currency
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.components :as c]
            [dinsro.components.buttons :as c.buttons]
            [dinsro.components.debug :as c.debug]
            [dinsro.events.debug :as e.debug]
            [dinsro.events.rates :as e.rates]
            [dinsro.spec.currencies :as s.currencies]
            [dinsro.spec.rates :as s.rates]
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
