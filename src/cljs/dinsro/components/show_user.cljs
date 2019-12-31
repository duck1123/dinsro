(ns dinsro.components.show-user
  (:require [dinsro.components.buttons :as c.buttons]
            [dinsro.components.debug :as c.debug]
            [dinsro.spec.users :as s.users]
            [dinsro.translations :refer [tr]]
            [orchestra.core :refer [defn-spec]]))

(defn-spec show-user vector?
  [user ::s.users/item]
  (let [{:keys [db/id dinsro.spec.users/name dinsro.spec.users/email]} user]
    [:<>
     [c.debug/debug-box user]
     [:p (tr [:name-label]) name]
     [:p (tr [:email-label]) email]
     (c.debug/hide [c.buttons/delete-user user])]))
