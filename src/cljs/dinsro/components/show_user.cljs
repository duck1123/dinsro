(ns dinsro.components.show-user
  (:require [clojure.spec.alpha :as s]
            [dinsro.components.buttons :as c.buttons]
            [dinsro.components.debug :as c.debug]
            [dinsro.events.users :as e.users]
            [dinsro.spec.users :as s.users]
            [dinsro.translations :refer [tr]]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]))

(defn-spec show-user vector?
  [user ::s.users/item]
  (let [{:keys [db/id dinsro.spec.users/name dinsro.spec.users/email]} user]
    [:<>
     [c.debug/debug-box user]
     [:p (tr [:name-label]) name]
     [:p (tr [:email-label]) email]
     (c.debug/hide [c.buttons/delete-user user])]))
