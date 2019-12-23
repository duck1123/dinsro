(ns dinsro.components.forms.create-currency
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.components :as c]
            [dinsro.components.debug :as c.debug]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]
            [taoensso.timbre :as timbre]))

(kf/reg-controller
 ::form-controller
 {:params (constantly true)
  :start [::set-defaults]})

(defn-spec form vector?
  []
  (let [form-data @(rf/subscribe [::form-data])]
    (when @(rf/subscribe [::shown?])
      [:<>
       [c/close-button ::set-shown?]
       [c.debug/debug-box form-data]
       [:form
        [c/text-input     (tr [:name])   ::name ::set-name]
        [c/primary-button (tr [:submit]) [::e.currencies/do-submit form-data]]]])))
