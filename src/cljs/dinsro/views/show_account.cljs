(ns dinsro.views.show-account
  (:require [dinsro.components.debug :as c.debug]
            [dinsro.components.forms.add-account-transactions :as c.f.add-account-transactions]
            [dinsro.components.index-transactions :refer [index-transactions]]
            [dinsro.components.show-account :refer [show-account]]
            [dinsro.events.accounts :as e.accounts]
            [dinsro.events.transactions :as e.transactions]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defn page
  [{{:keys [id]} :path-params :as match}]
  (let [account @(rf/subscribe [::e.accounts/item {:id (int id)}])]
    [:section.section>div.container>div.content
     [:div.box
      [:h1 "Show Account"]
      [show-account account]]
     [:div.box
      [:h2 "Transactions"]
      [c.f.add-account-transactions/form]
      [:hr]
      (let [items @(rf/subscribe [::e.transactions/items])]
        [c.debug/debug-box items]
        #_[index-transactions items])]]))
