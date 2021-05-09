(ns dinsro.views.index-users
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.index-users :as u.index-users]
   [taoensso.timbre :as log]))

(defsc IndexUsersPage
  [_this {::keys [users]}]
  {:componentDidMount
   (fn [this]
     (df/load! this :all-users u.index-users/IndexUserLine
               {:target [:page/id
                         ::page
                         ::users
                         :dinsro.ui.index-users/items]}))
   :ident (fn [] [:page/id ::page])
   :initial-state {::users {}}
   :query [{::users (comp/get-query u.index-users/IndexUsers)}]
   :route-segment ["users"]}
  (bulma/page
   (bulma/box
    (dom/h1 (tr [:users-page "Users Page"]))
    (dom/hr)
    (u.index-users/ui-index-users users))))

(def ui-page (comp/factory IndexUsersPage))
