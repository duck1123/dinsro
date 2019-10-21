(ns dinsro.views.show-user
  (:require [dinsro.components.show-user :refer [show-user]]
            [dinsro.events.users :as e.users]
            [re-frame.core :as rf]))

(defn page
  [{{:keys [id]} :path-params}]
  (let [user @(rf/subscribe [::e.users/item (int id)])]
    [:section.section>div.container>div.content
     [:h1 "Show User"]
     [show-user user]]))
