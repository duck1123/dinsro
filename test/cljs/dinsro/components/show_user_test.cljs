(ns dinsro.components.show-user-test
  (:require
   [devcards.core :refer-macros [defcard defcard-rg]]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.components.show-user :as c.show-user]
   [dinsro.spec.users :as s.users]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defcard-rg title
  [:div
   [:h1.title "Show User Components"]
   [:ul.box
    [:li
     [:a {:href "devcards.html#!/dinsro.components_test"}
      "Components"]]]

   [:ul.box
    [:li
     [:a {:href "devcards.html#!/dinsro.spec.users_test"}
      "Users Spec"]]]])

(let [user {::s.users/name "Bart"
               ::s.users/user {:db/id 1}
               ::s.users/currency {:db/id 1}}]
  (defcard user user)

  (defcard-rg show-user
    [error-boundary
     [c.show-user/show-user user]]))
