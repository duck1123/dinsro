(ns dinsro.components.forms.create-transaction-test
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [devcards.core :refer-macros [defcard-rg]]
            [dinsro.components :as c]
            [dinsro.components.datepicker :as c.datepicker]
            [dinsro.components.debug :as c.debug]
            [dinsro.components.forms.create-transaction :as c.f.create-transaction]
            [dinsro.events.forms.create-transaction :as e.f.create-transaction]
            [dinsro.events.rates :as e.rates]
            [dinsro.events.transactions :as e.transactions]
            [dinsro.spec.events.forms.create-transaction :as s.e.f.create-transaction]
            [dinsro.translations :refer [tr]]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]
            [taoensso.timbre :as timbre]))

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
