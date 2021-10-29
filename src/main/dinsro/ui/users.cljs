(ns dinsro.ui.users
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.container :as container]
   [com.fulcrologic.rad.container-options :as co]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.users :as m.users]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as log]))

(def users-path "/admin/users")

(def override-form true)

(form/defsc-form UserForm
  [this {::m.users/keys [name] :as props}]
  {fo/id           m.users/id
   fo/attributes   [m.users/name]
   fo/route-prefix "user"
   fo/title        "User"}
  (if override-form
    (form/render-layout this props)
    (dom/div {}
      (dom/p {} name))))

(def override-report true)

(report/defsc-report UsersReport
  [_this _props]
  {ro/form-links       {::m.users/name UserForm}
   ro/columns          [m.users/name]
   ro/source-attribute ::m.users/all-users
   ro/title            "Users"
   ro/row-pk           m.users/id
   ro/run-on-mount?    true})

(def ui-user-report (comp/factory UsersReport))

(report/defsc-report AdminIndexUsersReport
  [_this _props]
  {ro/columns          [m.users/name]
   ro/source-attribute ::m.users/all-users
   ro/title            "Users"
   ro/row-pk           m.users/id
   ro/run-on-mount?    true})

(def override-page true)

(container/defsc-container IndexUsersPage
  [this _props]
  {co/children {::users UsersReport}
   co/layout   [[{:id ::users :width 15}]]
   co/route    "users"}
  (if override-page
    (container/render-layout this)
    (dom/div {}
      (dom/h1 (tr [:users-page "Users Page"]))
      (dom/hr)
      (container/render-layout this))))

(def ui-page (comp/factory IndexUsersPage))
