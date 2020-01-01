(ns dinsro.components.forms.create-transaction-test
  (:require [devcards.core :refer-macros [defcard defcard-rg]]
            [dinsro.components.forms.create-transaction :as c.f.create-transaction]
            [dinsro.events.forms.create-transaction :as e.f.create-transaction]
            [dinsro.spec.events.forms.create-transaction :as s.e.f.create-transaction]
            [dinsro.spec :as ds]
            [dinsro.translations :refer [tr]]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defcard form-data
  (ds/gen-key ::e.f.create-transaction/form-data))

(defcard-rg form
  "**Create Transaction**"
  (fn [data]
    (with-redefs
      [rf/dispatch
       (fn [x]
         (timbre/infof "dispatch: %s" x))

       rf/subscribe
       (fn [x]
         (let [value
               (case x
                 [::s.e.f.create-transaction/value] 2
                 [::e.f.create-transaction/form-data] {}
                 [::e.f.create-transaction/shown?] false)]
           (timbre/infof "sub: %s => %s" x value)
           (atom value)))]
      (reagent.core/as-element
       [:<>
        [:div.box
         (c.f.create-transaction/form)]
        [:p.box (str @data)]])))
  {:form-data {::s.e.f.create-transaction/value 2}
   :shown? true
   :name "foo"})
