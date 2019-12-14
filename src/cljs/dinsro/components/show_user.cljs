(ns dinsro.components.show-user
  (:require [clojure.spec.alpha :as s]
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
    [:<>
     #_[:pre (str user)]
     [:<>
      [:p "name: " name]
      [:p "email: " email]
      [:a.button.is-danger {:on-click #(rf/dispatch [::e.users/do-delete-record id])}
       "Delete"]]]))
