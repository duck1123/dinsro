(ns dinsro.components.show-user
  (:require
   [dinsro.components.buttons :as c.buttons]
   [dinsro.components.debug :as c.debug]
   [dinsro.translations :refer [tr]]))

(defn show-user
  [store user]
  (let [{:keys [dinsro.specs.users/name dinsro.specs.users/email]} user]
    [:<>
     [:h1 name]
     [:p "(" email ")"]
     (c.debug/hide store [c.buttons/delete-user store user])]))
