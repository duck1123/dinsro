(ns dinsro.components.show-user)

(defn show-user
  [{:keys [db/id dinsro.spec.users/name dinsro.spec.users/email] :as user}]
  [:div
   [:p (str user)]
   [:p "Id: " id]
   [:p "name: " name]
   [:p "email: " email]])
