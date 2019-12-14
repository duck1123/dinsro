(ns dinsro.components.show-currency
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.components :as c]
            [dinsro.components.buttons :as c.buttons]
            [dinsro.components.forms.add-currency-rate :refer [add-currency-rate-form]]
            [dinsro.components.index-rates :refer [index-rates]]
            [dinsro.events.rates :as e.rates]
            [dinsro.spec.currencies :as s.currencies]
            [dinsro.spec.rates :as s.rates]
            [dinsro.translations :refer [tr]]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]))

(defn delete-button
  []
  [:button.button.is-danger "Delete"])

(defn-spec show-currency vector?
  [currency ::s.currencies/item]
  (let [currency-id (:db/id currency)
        name (::s.currencies/name currency)]
    [:<>
     [c/debug-box currency]
     [:p (tr [:name-label]) name]
     [c.buttons/delete-currency currency]]))
