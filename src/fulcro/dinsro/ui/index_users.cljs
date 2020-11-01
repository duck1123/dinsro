(ns dinsro.ui.index-users
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defsc IndexUserLine
  [_this {:user/keys [id name email]}]
  {:query [:user/id :user/name :user/email]
   :ident :user/id
   :initial-state {:user/id 1
                   :user/name "Foo"
                   :user/email "bob2example.com"}}
  (dom/tr
   (dom/td id)
   (dom/th name)
   (dom/th email)
   (dom/th (dom/button "Delete"))))

(def ui-index-user-line (comp/factory IndexUserLine))

(defsc IndexUsers
  [_this {users :users/list}]
  {:query [:users/list]
   :initial-state {:users/list []}}
  (let [path "/admin/users" #_(kf/path-for [:admin-index-users-page])]
    (dom/div
     (dom/h2 "Users")
     (if (seq users)
       (dom/div
        (dom/p
         (dom/a {:href path} "Users"))
        (dom/table
         (dom/thead
          (dom/tr
           (dom/th  (tr [:id-label]))
           (dom/th (tr [:name-label]))
           (dom/th (tr [:email-label]))
           (dom/th "Buttons")))
         (dom/tbody
          (map ui-index-user-line users))))
       (dom/div
        (dom/p (tr [:no-users])))))))

(def ui-index-users (comp/factory IndexUsers))
