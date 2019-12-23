(ns dinsro.components.forms.add-user-category
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.components :as c]
            [dinsro.components.debug :as c.debug]
            [dinsro.events.categories :as e.categories]
            [dinsro.events.users :as e.users]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.spec.users :as s.users]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]))

(defn-spec form vector?
  [user-id pos-int?]
  (let [form-data (assoc @(rf/subscribe [::form-data]) :user-id user-id)]
    (when @(rf/subscribe [::shown?])
      [:div
       [c/close-button ::set-shown?]
       [c.debug/debug-box form-data]
       [c/text-input (tr [:name]) ::name ::set-name]
       [c/primary-button (tr [:submit]) [::e.categories/do-submit form-data]]])))
