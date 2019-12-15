(ns dinsro.views.show-account
  (:require [dinsro.components :as c]
            [dinsro.components.buttons :as c.buttons]
            [dinsro.components.debug :as c.debug]
            [dinsro.components.forms.add-account-transactions :as c.f.add-account-transactions]
            [dinsro.components.index-transactions :refer [index-transactions]]
            [dinsro.components.show-account :refer [show-account]]
            [dinsro.events.accounts :as e.accounts]
            [dinsro.events.debug :as e.debug]
            [dinsro.events.transactions :as e.transactions]
            [dinsro.translations :refer [tr]]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defn-spec load-buttons vector?
  []
  (when @(rf/subscribe [::e.debug/shown?])
    [:div.box
     [c.buttons/fetch-accounts]
     [c.buttons/fetch-currencies]
     [c.buttons/fetch-transactions]]))

(defn-spec transactions-section vector?
  [account-id pos-int?]
  [:div.box
   [:h2
    (tr [:transactions])
    [c/show-form-button
     ::c.f.add-account-transactions/shown?
     ::c.f.add-account-transactions/set-shown?]]
   [c.f.add-account-transactions/form]
   [:hr]
   (let [items @(rf/subscribe [::e.transactions/items])]
     [c.debug/debug-box items]
     [index-transactions items])])

(defn-spec page vector?
  [match any?]
  (let [{{:keys [id]} :path-params} match
        id (int id)
        account @(rf/subscribe [::e.accounts/item {:id id}])]
    [:section.section>div.container>div.content
     [load-buttons]
     [:div.box
      [:h1 (tr [:show-account])]
      (when account
        [show-account account])]
     [transactions-section id]]))
