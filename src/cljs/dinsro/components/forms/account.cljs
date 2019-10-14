(ns dinsro.components.forms.account
  (:require [dinsro.components :as c]
            [re-frame.core :as rf]))

(rf/reg-sub ::name ::name)

(defn new-account-form
  []
  [:div
   [:p "New Account Form"]
   [:form
    [c/text-input "Name" ::name ::change-name]]])
