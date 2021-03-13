(ns dinsro.ui.show-user
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.users :as m.users]
   [dinsro.translations :refer [tr]]))

(def form-toggle-sm ::form-toggle)

(defsc ShowUser
  [_this {::m.users/keys [name email]}]
  {:ident ::m.users/id
   :initial-state {::m.users/id    0
                   ::m.users/name  "initial-name"
                   ::m.users/email "initial-email"}
   :query [::m.users/email
           ::m.users/id
           ::m.users/name]}
  (dom/div
   (dom/h1 name)
   (dom/p (str "(" email ")"))
   (dom/button :.button.is-danger "Delete User")))

(def ui-show-user (comp/factory ShowUser))
