(ns dinsro.components.forms.create-rate-source
  (:require [clojure.spec.alpha :as s]
            [dinsro.components :as c]
            [dinsro.components.datepicker :as c.datepicker]
            [dinsro.components.debug :as c.debug]
            [dinsro.events.forms.create-rate-source :as e.f.create-rate-source]
            [dinsro.events.rate-sources :as e.rate-sources]
            [dinsro.spec.events.forms.create-rate-source :as s.e.f.create-rate-source]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]
            [taoensso.timbre :as timbre]))

(kf/reg-controller
 ::form-controller
 {:params (constantly true)
  :start [::e.f.create-rate-source/init-form]})

(defn-spec form vector?
  []
  (let [form-data @(rf/subscribe [::e.f.create-rate-source/form-data])]
    (when @(rf/subscribe [::s.e.f.create-rate-source/shown?])
      [:<>
       [:a.delete.is-pulled-right {:on-click #(rf/dispatch [::s.e.f.create-rate-source/set-shown? false])}]
       [c/text-input (tr [:name])
        ::s.e.f.create-rate-source/name ::s.e.f.create-rate-source/set-name]
       [:div.field>div.control
        [c.debug/debug-box form-data]]
       [:div.field>div.control
        [c/primary-button (tr [:submit]) [::e.rate-sources/do-submit form-data]]]])))
