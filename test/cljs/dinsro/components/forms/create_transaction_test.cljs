(ns dinsro.components.forms.create-transaction-test
  (:require [devcards.core :refer-macros [defcard]]
            [dinsro.components.forms.create-transaction :as c.f.create-transaction]
            [dinsro.events.forms.create-transaction :as e.f.create-transaction]
            [dinsro.spec.events.forms.create-transaction :as s.e.f.create-transaction]
            [dinsro.spec :as ds]
            [dinsro.translations :refer [tr]]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [taoensso.timbre :as timbre]))

(defcard a
  (ds/gen-key ::e.f.create-transaction/form-data))

(defcard b
  "foo"
  (r/as-element [:h1.title "foo bar"]))

(defn mock-dispatch
  [x]
  (timbre/infof "dispatch: %s" x))

(defn mock-subscribe
  [x]
  (let [value
        (case x
          [::s.e.f.create-transaction/value] 2
          [::e.f.create-transaction/form-data] {}
          [::e.f.create-transaction/shown?] false)]
    (timbre/infof "sub: %s => %s" x value)
    (atom value)))

(defcard create-transaction-card
  "**Create Transaction**"
  (fn [data]
    (with-redefs [rf/dispatch mock-dispatch
                  rf/subscribe mock-subscribe]
      (reagent.core/as-element
       [:<>
        [:div.box
         [c.f.create-transaction/form]]
        [:p.box (str @data)]])))
  {:form-data {::s.e.f.create-transaction/value 2}
   :shown? true
   :name "foo"})
