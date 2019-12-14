(ns dinsro.components.show-user
  (:require [clojure.spec.alpha :as s]
            [dinsro.components.forms.add-user-account :refer [add-user-account]]
            [dinsro.components.index-accounts :refer [index-accounts]]
            [dinsro.events.accounts :as e.accounts]
            [dinsro.events.users :as e.users]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.spec.users :as s.users]
            [dinsro.translations :refer [tr]]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]))

(defn delete-button
  [id]
  [:a.button.is-danger {:on-click #(rf/dispatch [::e.users/do-delete-record id])}
   (tr [:delete])])

(defn-spec show-user vector?
  [user ::s.users/item]
  (let [{:keys [db/id dinsro.spec.users/name dinsro.spec.users/email]} user]
    [:<>
     #_[:pre (str user)]
     [:<>
      [:p (tr [:name-label]) name]
      [:p (tr [:email-label]) email]
      [delete-button id]]]))
