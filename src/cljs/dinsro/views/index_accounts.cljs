(ns dinsro.views.index-accounts
  (:require [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components.forms.account :refer [new-account-form]]
            [dinsro.components.index-accounts :refer [index-accounts]]
            [dinsro.events.accounts :as e.accounts]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(rf/reg-event-fx
 ::init-page
 (fn-traced [{:keys [db]} _]
   {:dispatch [::e.accounts/do-fetch-accounts]}))

(kf/reg-controller
 ::page-controller
 {:params #(when (= (get-in % [:data :name]) ::page) true)
  :start [::init-page]})

(defn page
  []
  [:section.section>div.container>div.content
   [:h1 "Index Accounts"]
   [:a.button {:on-click #(rf/dispatch [::init-page])} "Load"]
   [new-account-form]
   [index-accounts]])
