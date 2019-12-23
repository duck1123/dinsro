(ns dinsro.components.forms.add-user-account
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.components :as c]
            [dinsro.components.debug :as c.debug]
            [dinsro.events.accounts :as e.accounts]
            [dinsro.events.forms.add-user-account :as e.f.add-user-account]
            [dinsro.events.users :as e.users]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.spec.users :as s.users]
            [dinsro.specs :as ds]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]))

(defn-spec form vector?
  [id ::ds/id]
  (let [form-data (assoc @(rf/subscribe [::e.f.add-user-account/form-data]) :user-id id)]
    (when @(rf/subscribe [::e.f.add-user-account/shown?])
      [:<>
       [c/close-button ::e.f.add-user-account/set-shown?]
       [c.debug/debug-box form-data]
       [c/text-input (tr [:name])
        ::e.f.add-user-account/name ::e.f.add-user-account/set-name]
       [c/number-input (tr [:initial-value])
        ::e.f.add-user-account/initial-value ::e.f.add-user-account/set-initial-value]
       [c/currency-selector (tr [:currency])
        ::e.f.add-user-account/currency-id ::e.f.add-user-account/set-currency-id]
       [c/primary-button (tr [:submit]) [::e.accounts/do-submit form-data]]])))
