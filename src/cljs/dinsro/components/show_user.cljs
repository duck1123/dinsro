(ns dinsro.components.show-user
  (:require [clojure.spec.alpha :as s]
            [dinsro.components.forms.create-account :refer [create-user-account]]
            [dinsro.components.forms.add-user-account :refer [add-user-account]]
            [dinsro.components.index-accounts :refer [index-accounts]]
            [dinsro.events.accounts :as e.accounts]
            [dinsro.events.users :as e.users]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.spec.users :as s.users]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]))

(defn-spec show-user vector?
  [user any? #_::s.users/item
   accounts any? #_(s/coll-of ::s.accounts/item)]
  (let [{:keys [db/id dinsro.spec.users/name dinsro.spec.users/email]} user]
    [:div
     #_[:pre (str user)]
     [:div.box
      #_[:p "Id: " id]
      [:p "name: " name]
      [:p "email: " email]
      [:a.button.is-danger {:on-click #(rf/dispatch [::e.users/do-delete-record id])}
       "Delete"]]
     #_[:pre (str accounts)]
     #_[create-user-account]
     #_[:button.button {:on-click #(rf/dispatch [::e.accounts/do-fetch-index])} "Load Accounts"]
     [:div.box
      [add-user-account id]
      [:hr]
      [index-accounts accounts]]]))

;; (defn-spec show-user vector?
;;   [user ::s.users/item]
;;   (let [accounts [{:db/id 7}]]
;;     [show-user- user accounts]))
