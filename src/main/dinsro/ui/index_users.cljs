(ns dinsro.ui.index-users
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.users :as m.users]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]
   [taoensso.timbre :as timbre]))

(def form-toggle-sm ::form-toggle)

(defsc IndexUserLine
  [_this {::m.users/keys [link username]}]
  {:ident         ::m.users/username
   :initial-state {::m.users/link     {}
                   ::m.users/username ""}
   :query         [::m.users/username
                   {::m.users/link (comp/get-query u.links/ui-user-link)}]}
  (dom/tr {}
    (dom/th (u.links/ui-user-link (first link)))
    (dom/th (u.buttons/ui-delete-user-button {::m.users/username username}))))

(def ui-index-user-line (comp/factory IndexUserLine {:keyfn ::m.users/username}))

(def users-path "/admin/users")

(defsc IndexUsers
  [_this {::keys [items]}]
  {:initial-state {::items []}
   :query         [{::items (comp/get-query IndexUserLine)}]}
  (if-not (seq items)
    (dom/div {} (dom/p (tr [:no-users])))
    (dom/div {}
      (dom/p
       (dom/a {:href users-path} "Users"))
      (dom/table :.table
        (dom/thead {}
          (dom/tr {}
            (dom/th (tr [:username]))
            (dom/th "Buttons")))
        (dom/tbody {}
          (map ui-index-user-line items))))))

(def ui-index-users (comp/factory IndexUsers))
