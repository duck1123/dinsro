(ns dinsro.ui.show-user
  (:require
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]))

(defn show-user
  [store user]
  (let [{:keys [dinsro.specs.users/name dinsro.specs.users/email]} user]
    [:<>
     [:h1 name]
     [:p "(" email ")"]
     (u.debug/hide store [u.buttons/delete-user store user])]))
