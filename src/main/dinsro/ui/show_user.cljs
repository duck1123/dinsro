(ns dinsro.ui.show-user
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.users :as m.users]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]))

(def form-toggle-sm ::form-toggle)

(defsc ShowUser
  [_this {::m.users/keys [id]}]
  {:ident         ::m.users/id
   :initial-state {::m.users/id "admin"}
   :query         [::m.users/id]}
  (dom/div {}
    (dom/h1 id)
    (u.buttons/ui-delete-user-button {::m.users/id id})))

(def ui-show-user (comp/factory ShowUser))
