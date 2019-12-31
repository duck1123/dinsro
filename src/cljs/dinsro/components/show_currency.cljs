(ns dinsro.components.show-currency
  (:require [clojure.spec.alpha :as s]
            [dinsro.components.buttons :as c.buttons]
            [dinsro.components.debug :as c.debug]
            [dinsro.events.debug :as e.debug]
            [dinsro.spec.currencies :as s.currencies]
            [dinsro.translations :refer [tr]]
            [re-frame.core :as rf]))

(defn show-currency
  [currency]
  (let [name (::s.currencies/name currency)]
    [:<>
     [c.debug/debug-box currency]
     [:p (tr [:name-label]) name]
     (when @(rf/subscribe [::e.debug/shown?])
       [c.buttons/delete-currency currency])]))

(s/fdef show-currency
  :args (s/cat :currency ::s.currencies/item)
  :ret vector?)
