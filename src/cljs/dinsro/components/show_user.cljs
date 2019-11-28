(ns dinsro.components.show-user
  (:require [clojure.spec.alpha :as s]
            [dinsro.components.forms.account :refer [create-user-account]]
            [dinsro.components.index-accounts :refer [index-accounts]]
            [dinsro.events.accounts :as e.accounts]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.spec.users :as s.users]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]))

(defn-spec show-user vector?
  [user ::s.users/item
   accounts (s/coll-of ::s.accounts/item)]
  (let [{:keys [db/id dinsro.spec.users/name dinsro.spec.users/email]} user]
    [:div
     [:pre (str user)]
     [:p "Id: " id]
     [:p "name: " name]
     [:p "email: " email]
     [:pre (str accounts)]
     [create-user-account]
     [:button.button {:on-click #(rf/dispatch [::e.accounts/do-fetch-index])} "Load Accounts"]
     [index-accounts accounts]]))

;; (defn-spec show-user vector?
;;   [user ::s.users/item]
;;   (let [accounts [{:db/id 7}]]
;;     [show-user- user accounts]))
