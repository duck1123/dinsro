(ns dinsro.components.admin-index-users
(:require
   [dinsro.components.index-users :as c.index-users]
   [dinsro.events.users :as e.users]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defn section
  [store]
  [:div.box
   [:h2 (tr [:users])]
   (let [users @(st/subscribe store [::e.users/items])]
     [c.index-users/index-users store users])])
