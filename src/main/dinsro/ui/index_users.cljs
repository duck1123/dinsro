(ns dinsro.ui.index-users
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.users :as m.users]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]
   [taoensso.timbre :as log]))

(def form-toggle-sm ::form-toggle)

(defsc IndexUserLine
  [_this {::m.users/keys [id link]}]
  {:ident         ::m.users/id
   :initial-state {::m.users/id   ""
                   ::m.users/link {}}
   :query         [::m.users/id
                   {::m.users/link (comp/get-query u.links/ui-user-link)}]}
  (dom/tr {}
    (dom/th (u.links/ui-user-link link))
    (dom/th (u.buttons/ui-delete-user-button {::m.users/id id}))))

(def ui-index-user-line (comp/factory IndexUserLine {:keyfn ::m.users/id}))

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
      (dom/table :.table.ui
        (dom/thead {}
          (dom/tr {}
            (dom/th (tr [:username]))
            (dom/th "Buttons")))
        (dom/tbody {}
          (map ui-index-user-line items))))))

(def ui-index-users (comp/factory IndexUsers))
