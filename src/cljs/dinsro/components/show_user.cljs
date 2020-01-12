(ns dinsro.components.show-user
  (:require
   [dinsro.components.buttons :as c.buttons]
   [dinsro.components.debug :as c.debug]
   [dinsro.translations :refer [tr]]))

(defn show-user
  [user]
  (let [{:keys [dinsro.spec.users/name dinsro.spec.users/email]} user]
    [:<>
     [c.debug/debug-box user]
     [:p (tr [:name-label]) name]
     [:p (tr [:email-label]) email]
     (c.debug/hide [c.buttons/delete-user user])]))
