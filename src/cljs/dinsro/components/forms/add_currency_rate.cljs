(ns dinsro.components.forms.add-currency-rate
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
            [dinsro.spec.currencies :as s.currencies]
            [dinsro.spec.rates :as s.rates]
            [dinsro.specs :as ds]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]
            [taoensso.timbre :as timbre]))

(defn-spec form vector?
  [currency-id ::ds/id]
  (when @(rf/subscribe [::shown?])
    (let [form-data (assoc @(rf/subscribe [::form-data]) :currency-id currency-id)]
      [:<>
       [c/close-button ::set-shown?]
       [:div.field>div.control
        [c/number-input (tr [:rate]) ::rate ::set-rate]]
       [:div.field>div.control
        [c.datepicker/datepicker
         {:on-select #(rf/dispatch [::set-date %])}]]
       [c.debug/debug-box form-data]
       [:div.field>div.control
        [c/primary-button (tr [:submit]) [::e.rates/do-submit form-data]]]])))
