(ns dinsro.views.index-transactions
  (:require [dinsro.components :as c]
            [dinsro.components.buttons :as c.buttons]
            [dinsro.components.forms.create-transaction :as c.f.create-transaction]
            [dinsro.components.index-transactions :refer [index-transactions]]
            [dinsro.events.accounts :as e.accounts]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.events.debug :as e.debug]
            [dinsro.events.transactions :as e.transactions]
            [dinsro.spec.events.forms.create-transaction :as s.e.f.create-transaction]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defn init-page
  [_ _]
  {:document/title "Index Transactions"
   :dispatch-n [[::e.transactions/do-fetch-index]
                [::e.accounts/do-fetch-index]
                [::e.currencies/do-fetch-index]]})

(kf/reg-event-fx ::init-page init-page)

(kf/reg-controller
 ::page-controller
 {:params (c/filter-page :index-transactions-page)
  :start [::init-page]})

(defn load-buttons
  []
  (when @(rf/subscribe [::e.debug/shown?])
    [:div.box
     [c.buttons/fetch-transactions]
     [c.buttons/fetch-accounts]
     [c.buttons/fetch-currencies]]))

(defn page
  []
  [:section.section>div.container>div.content
   [load-buttons]
   (let [transactions (or @(rf/subscribe [::e.transactions/items]) [])]
     [:div.box
      [:h1
       (tr [:index-transactions-title "Index Transactions"])
       [c/show-form-button ::e.f.create-transaction/shown? ::e.f.create-transaction/set-shown?]]
      [c.f.create-transaction/form]
      [:hr]
      [index-transactions transactions]])])
