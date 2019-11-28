(ns dinsro.views.show-user
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.components.show-user :refer [show-user]]
            [dinsro.events.accounts :as e.accounts]
            [dinsro.events.users :as e.users]
            [dinsro.specs :as ds]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]))

(s/def :show-user-view/id          pos-int?)
(s/def :show-user-view/path-params (s/keys :req-un [:show-user-view/id]))
(s/def ::view-map                  (s/keys :req-un [:show-user-view/path-params]))

(defn page
  [{{:keys [id]} :path-params}]
  (let [user @(rf/subscribe [::e.users/item (int id)])
        user-id (:db/id user)
        accounts @(rf/subscribe [::e.accounts/items-by-user user-id])]
    [:section.section>div.container>div.content
     [:h1 "Show User"]
     (if (nil? user)
       [:p "User not loaded"]
       [show-user user accounts])]))
