(ns dinsro.components.forms.add-user-transaction
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.components :as c]
            [dinsro.components.forms.create-category :as c.f.create-category]
            [dinsro.components.debug :as c.debug]
            [dinsro.events.users :as e.users]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.spec.actions.transactions :as s.a.transactions]
            [dinsro.spec.users :as s.users]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]))

(defn-spec form vector?
  []
  (let [form-data @(rf/subscribe [::form-data])]
    (when @(rf/subscribe [::shown?])
      [:div
       [c/close-button ::set-shown?]
       [c.debug/debug-box form-data]
       [:p "Form"]
       [:div.field>div.control
        [c/number-input (tr [:value]) ::value ::set-value]]
       [:div.field>div.control
        [c/currency-selector (tr [:currency]) ::currency-id ::set-currency-id]]
       [:div.field>div.control
        [c/primary-button (tr [:submit]) [::submit-clicked]]]])))
