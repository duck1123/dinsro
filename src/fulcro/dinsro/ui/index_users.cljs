(ns dinsro.ui.index-users
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.users :as m.users]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defsc IndexUserLine
  [_this {::m.users/keys [email id name]}]
  {:ident ::m.users/id
   :initial-state {::m.users/email ""
                   ::m.users/id    0
                   ::m.users/name  ""}
   :query [::m.users/email
           ::m.users/id
           ::m.users/name]}
  (dom/tr
   (dom/td id)
   (dom/th name)
   (dom/th email)
   (dom/th (dom/button :.button.is-danger "Delete"))))

(def ui-index-user-line (comp/factory IndexUserLine {:keyfn ::m.users/id}))

(def users-path "/admin/users")

(defsc IndexUsers
  [_this {::keys [items]}]
  {:initial-state {::items []}
   :query [{::items (comp/get-query IndexUserLine)}]}
  (if-not (seq items)
    (dom/div (dom/p (tr [:no-users])))
    (dom/div
     (dom/p
      (dom/a {:href users-path} "Users"))
     (dom/table
      :.table
      (dom/thead
       (dom/tr
        (dom/th (tr [:id-label]))
        (dom/th (tr [:name-label]))
        (dom/th (tr [:email-label]))
        (dom/th "Buttons")))
      (dom/tbody
       (map ui-index-user-line items))))))

(def ui-index-users (comp/factory IndexUsers))
