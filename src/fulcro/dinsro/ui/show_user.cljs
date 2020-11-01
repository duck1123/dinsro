(ns dinsro.ui.show-user
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]))

(defsc ShowUser
  [_this {::keys [name email]}]
  {:query [::name ::email]
   :initial-state {::name "initial-name"
                   ::email "initial-email"}}
  (dom/div
   (dom/h1 name)
   (dom/p (str "(" email ")"))
   (dom/button "Delete User")))

(def ui-show-user (comp/factory ShowUser))
