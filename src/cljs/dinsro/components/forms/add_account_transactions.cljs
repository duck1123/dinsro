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
            [dinsro.spec.currencies :as s.currencies]
            [dinsro.spec.rates :as s.rates]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]
            [taoensso.timbre :as timbre]))

(s/def ::shown? boolean?)
(rfu/reg-basic-sub ::shown?)
(rfu/reg-set-event ::shown?)

(defn toggle
  [cofx event]
  (let [{:keys [db]} cofx]
    {:db (update db ::shown? not)}))

(kf/reg-event-fx ::toggle toggle)

(defn toggle-button
  []
  (let [shown? @(rf/subscribe [::shown?])]
    [:a {:on-click #(rf/dispatch [::toggle])}
     (if shown?
       [:span.icon>i.fas.fa-chevron-down]
       [:span.icon>i.fas.fa-chevron-right])]))

(defn form
  []
  [:<>
   #_(when-not @(rf/subscribe [::shown?])
     [:div.is-pulled-right [toggle-button]])
   (when @(rf/subscribe [::shown?])
         [:div
          [:p "Form"]])])
