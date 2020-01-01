(ns dinsro.components.show-currency
  (:require [clojure.spec.alpha :as s]
            [dinsro.components.buttons :as c.buttons]
            [dinsro.components.debug :as c.debug]
            [dinsro.spec.currencies :as s.currencies]
            [dinsro.translations :refer [tr]]))

(defn show-currency
  [currency]
  (let [name (::s.currencies/name currency)]
    [:<>
     [c.debug/debug-box currency]
     [:p (tr [:name-label]) name]
     (c.debug/hide [c.buttons/delete-currency currency])]))

(s/fdef show-currency
  :args (s/cat :currency ::s.currencies/item)
  :ret vector?)
