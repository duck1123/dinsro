(ns dinsro.views.index-users
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.index-users :as u.index-users]
   [taoensso.timbre :as timbre]))

(defsc IndexUsersPage
  [_this {::keys [users]}]
  {:query [{::users (comp/get-query u.index-users/IndexUsers)}]
   :ident (fn [] [:page/id ::page])
   :initial-state
   (fn [_]
     {::users (comp/get-initial-state u.index-users/IndexUsers)})
   :route-segment ["users"]}
  (bulma/section
   (bulma/container
    (bulma/content
     (bulma/box
      (dom/h1 (tr [:users-page "Users Page"]))
      (dom/hr)
      (u.index-users/ui-index-users users))))))

(def ui-page (comp/factory IndexUsersPage))
