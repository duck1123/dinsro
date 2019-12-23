(ns dinsro.components.forms.add-account-transactions
  (:require [cljc.java-time.instant :as instant]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components :as c]
            [dinsro.components.buttons :as c.buttons]
            [dinsro.components.forms.create-rate :as c.f.create-rate]
            [dinsro.components.datepicker :as c.datepicker]
            [dinsro.components.debug :as c.debug]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.events.rates :as e.rates]
            [dinsro.events.transactions :as e.transactions]
            [dinsro.spec.currencies :as s.currencies]
            [dinsro.spec.events.forms.add-account-transactions :as s.e.f.add-account-transactions]
            [dinsro.spec.rates :as s.rates]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]
            [taoensso.timbre :as timbre]))

(defn-spec form vector?
  []
  [:<>
   (let [form-data @(rf/subscribe [::form-data])]
     (when @(rf/subscribe [::shown?])
       [:div
        [c/close-button ::set-shown?]
        [c.debug/debug-box form-data]
        [:div.field>div.control
         [c/number-input (tr [:value]) ::value ::set-value]]
        [:div.field>div.control
         [c/currency-selector (tr [:currency]) ::currency-id ::set-currency-id]]
        [:div.field>div.control
         [c.datepicker/datepicker
          {:on-select #(rf/dispatch [::set-date %])}]]
        [:div.field>div.control
         [c/primary-button (tr [:submit]) [::e.transactions/do-submit form-data]]]]))])
