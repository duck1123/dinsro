(ns dinsro.components.forms.create-account
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components :as c]
            [dinsro.components.debug :as c.debug]
            [dinsro.events.accounts :as e.accounts]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]
            [taoensso.timbre :as timbre]))

(defn-spec form vector?
  []
  (let [form-data @(rf/subscribe [::form-data])]
    (when @(rf/subscribe [::shown?])
      [:<>
       [c/close-button ::set-shown?]
       [c.debug/debug-box form-data]
       [c/text-input (tr [:name]) ::name ::set-name]
       [c/number-input (tr [:initial-value]) ::initial-value ::set-initial-value]
       [c/currency-selector (tr [:currency]) ::currency-id ::set-currency-id]
       [c/user-selector (tr [:user]) ::user-id ::set-user-id]
       [c/primary-button (tr [:submit]) [::e.accounts/do-submit form-data]]])))
