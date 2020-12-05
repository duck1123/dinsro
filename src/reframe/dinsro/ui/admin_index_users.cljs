(ns dinsro.ui.admin-index-users
  (:require
   [dinsro.events.users :as e.users]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.index-users :as u.index-users]
   [taoensso.timbre :as timbre]))

(defn section
  [store]
  [:div.box
   [:h2 (tr [:users])]
   (let [users @(st/subscribe store [::e.users/items])]
     [u.index-users/index-users store users])])
