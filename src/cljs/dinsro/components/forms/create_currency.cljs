(ns dinsro.components.forms.create-currency
  (:require             [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components :as c]
                        [dinsro.events.currencies :as e.currencies]
                     [kee-frame.core :as kf]
                        [re-frame.core :as rf]
                        [taoensso.timbre :as timbre]
                        )
  )

(c/reg-field ::name          "")
(kf/reg-event-db ::change-name   (fn-traced [db [value]] (assoc db ::name value)))

(rf/reg-sub
 ::form-data
 :<- [::name]
 (fn-traced [name]
   {:name name}))

(kf/reg-event-fx
 ::submit-clicked
 (fn-traced
   [_ _]
   {:dispatch [::e.currencies/do-submit @(rf/subscribe [::form-data])]}))



(defn create-currency
  []
  [:div
   [:p "Create Currency"]
   [:pre (str @(rf/subscribe [::form-data]))]
   [:form
    [c/text-input        "Name"     ::name        ::change-name]
    [c/primary-button "Submit" ::submit-clicked]]])
