(ns dinsro.views.index-transactions
  (:require [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components.buttons :as c.buttons]
            [dinsro.components.forms.create-transaction :as c.f.create-transaction]
            [dinsro.components.index-transactions :refer [index-transactions]]
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
  {:db (assoc db ::e.transactions/items [example-transaction])
   :dispatch [::e.transactions/do-fetch-index]})

(defn load-buttons
  []
  [:div.box
   [c.buttons/fetch-transactions]
   [c.buttons/fetch-accounts]
   [c.buttons/fetch-currencies]
   [c.buttons/toggle-debug]])

(defn show-form-button
  []
  (when-not @(rf/subscribe [::c.f.create-transaction/shown?])
    [:a.is-pulled-right {:on-click #(rf/dispatch [::c.f.create-transaction/set-shown? true])}
     (tr [:show-form "Show Form"])]))

(defn page
  []
  [:section.section>div.container>div.content
   [load-buttons]
   (let [transactions @(rf/subscribe [::e.transactions/items])]
     [:div.box
      [:h1
       (tr [:index-transactions-title "Index Transactions"])
       [show-form-button]]
      [c.f.create-transaction/create-transaction-form]
      [:hr]
      [index-transactions transactions]])])
