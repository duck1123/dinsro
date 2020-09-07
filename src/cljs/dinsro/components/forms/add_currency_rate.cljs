(ns dinsro.components.forms.add-currency-rate
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.components :as c]
   [dinsro.components.datepicker :as c.datepicker]
   [dinsro.events.forms.add-currency-rate :as e.f.add-currency-rate]
   [dinsro.events.rates :as e.rates]
   [dinsro.spec :as ds]
   [dinsro.spec.events.forms.create-rate :as s.e.f.create-rate]
   [dinsro.spec.rate-sources :as s.rate-sources]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [kee-frame.core :as kf]
   [taoensso.timbre :as timbre]))

(kf/reg-controller
 ::form-controller
 {:params (constantly true)
  :start [::e.f.add-currency-rate/init-form]})

(defn form
  [store currency-id]
  (when @(st/subscribe store [::e.f.add-currency-rate/shown?])
    (let [form-data @(st/subscribe store [::e.f.add-currency-rate/form-data currency-id])
          rate-sources (ds/gen-key (s/coll-of ::s.rate-sources/item))]
      [:<>
       [c/close-button store ::e.f.add-currency-rate/set-shown?]
       [:div.field>div.control
        [c/number-input store (tr [:rate]) ::s.e.f.create-rate/rate]]
       [c/rate-source-selector- store
        (tr [:rate-source])
        ::s.e.f.create-rate/rate-source-id
        ::s.e.f.create-rate/set-rate-source-id
        rate-sources]
       [:div.field
        [:label.label (tr [:date])]
        [:div.control
         [c.datepicker/datepicker {:on-select #(st/dispatch store [::s.e.f.create-rate/set-date %])}]]]
       [:div.field>div.control
        [c/primary-button store (tr [:submit]) [::e.rates/do-submit form-data]]]])))
