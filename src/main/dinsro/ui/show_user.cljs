(ns dinsro.ui.show-user
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.users :as m.users]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]))

(def form-toggle-sm ::form-toggle)

(defsc ShowUser
  [_this {::m.users/keys [username]}]
  {:ident         ::m.users/username
   :initial-state {::m.users/username "admin"}
   :query         [::m.users/username]}
  (dom/div
   (dom/h1 username)
   (dom/p (str "(" username ")"))
   (u.buttons/ui-delete-user-button {::m.users/username username})))

(def ui-show-user (comp/factory ShowUser))
