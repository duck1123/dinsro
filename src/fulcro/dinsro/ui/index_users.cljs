(ns dinsro.ui.index-users
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.users :as m.users]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defsc IndexUserLine
  [_this {::m.users/keys [id name email]}]
  {:query [::m.users/id ::m.users/name ::m.users/email]
   :ident ::m.users/id
   :initial-state {::m.users/email ""
                   ::m.users/id    0
                   ::m.users/name  ""}}
  (dom/tr
   (dom/td id)
   (dom/th name)
   (dom/th email)
   (dom/th (dom/button :.button.is-danger "Delete"))))

(def ui-index-user-line (comp/factory IndexUserLine {:keyfn ::m.users/id}))

(defsc IndexUsers
  [_this {users :users/list}]
  {:initial-state {:users/list []}
   :query [:users/list]}
  (let [path "/admin/users"]
    (dom/div
     (dom/h2 "Users")
     (if (seq users)
       (dom/div
        (dom/p
         (dom/a {:href path} "Users"))
        (dom/table
         :.table
         (dom/thead
          (dom/tr
           (dom/th (tr [:id-label]))
           (dom/th (tr [:name-label]))
           (dom/th (tr [:email-label]))
           (dom/th "Buttons")))
         (dom/tbody
          (map ui-index-user-line users))))
       (dom/div
        (dom/p (tr [:no-users])))))))

(def ui-index-users (comp/factory IndexUsers))
