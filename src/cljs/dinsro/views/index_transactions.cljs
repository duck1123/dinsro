(ns dinsro.views.index-transactions
  (:require [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components :as c]
            [dinsro.components.buttons :as c.buttons]
            [dinsro.components.debug :as c.debug]
            [dinsro.components.forms.create-transaction :as c.f.create-transaction]
            [dinsro.components.index-transactions :refer [index-transactions]]
            [dinsro.events.debug :as e.debug]
            [dinsro.events.transactions :as e.transactions]
            [dinsro.spec.transactions :as s.transactions]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(def example-transaction
  {:db/id 1
   ::s.transactions/value 130000
   ::s.transactions/currency {:db/id 53}
   ::s.transactions/account {:db/id 12}})

(defn init-page
  [{:keys [db]} _]
  {
   ;; :db (assoc db ::e.transactions/items [example-transaction])
   :document/title "Index Transactions"
   :dispatch [::e.transactions/do-fetch-index]})

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
     [c.buttons/fetch-currencies]
     [c.buttons/toggle-debug]]))

(defn page
  []
  [:section.section>div.container>div.content
   [load-buttons]
   (let [transactions @(rf/subscribe [::e.transactions/items])]
     [:div.box
      [:h1
       (tr [:index-transactions-title "Index Transactions"])
       [c/show-form-button ::c.f.create-transaction/shown? ::c.f.create-transaction/set-shown?]]
      [c.f.create-transaction/create-transaction-form]
      [:hr]
      [index-transactions transactions]])])
