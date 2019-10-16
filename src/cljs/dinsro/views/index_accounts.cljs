(ns dinsro.views.index-accounts
  (:require [dinsro.components.forms.account :refer [new-account-form]]
            [dinsro.components.index-accounts :refer [index-accounts]]
            [dinsro.events.accounts :as e.accounts]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            ;; [reagent.core :as r]
            [taoensso.timbre :as timbre]))

(rf/reg-event-fx
 ::init-page
 (fn [{:keys [db]} _]
   {:db (-> db
            (assoc :failed false)
            (assoc ::e.accounts/accounts [])
            (assoc ::loading false))
    :dispatch [::e.accounts/do-fetch-accounts]}))

(kf/reg-controller
 ::page-controller
 {:params (fn [{{:keys [name]} :data}]
            (timbre/spy :info (or (= (timbre/spy :info name) ::page) nil)))
  :start [::init-page]})

(defn page
  []
  [:section.section>div.container>div.content
   [:h1 "Index Accounts"]
   [index-accounts]
   [new-account-form]])
