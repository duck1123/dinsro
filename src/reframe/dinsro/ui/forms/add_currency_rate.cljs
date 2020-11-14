(ns dinsro.ui.forms.add-currency-rate
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.events.forms.add-currency-rate :as e.f.add-currency-rate]
   [dinsro.events.rates :as e.rates]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.specs :as ds]
   [dinsro.specs.events.forms.create-rate :as s.e.f.create-rate]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.datepicker :as u.datepicker]
   [dinsro.ui.inputs :as u.inputs]
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
          rate-sources (ds/gen-key (s/coll-of ::m.rate-sources/item))]
      [:<>
       [u.buttons/close-button store ::e.f.add-currency-rate/set-shown?]
       [:div.field>div.control
        [u.inputs/number-input store (tr [:rate]) ::s.e.f.create-rate/rate]]
       [u.inputs/rate-source-selector- store
        (tr [:rate-source])
        ::s.e.f.create-rate/rate-source-id
        ::s.e.f.create-rate/set-rate-source-id
        rate-sources]
       [:div.field
        [:label.label (tr [:date])]
        [:div.control
         [u.datepicker/datepicker {:on-select #(st/dispatch store [::s.e.f.create-rate/set-date %])}]]]
       [:div.field>div.control
        [u.inputs/primary-button store (tr [:submit]) [::e.rates/do-submit form-data]]]])))
