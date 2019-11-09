(ns dinsro.components.show-user)

(defn show-user
  [{:keys [dinsro.model.user/id name email] :as user}]
  [:div
   #_[:p (str user)]
   [:p "Id: " id]
   [:p "name: " name]
   [:p "email: " email]])
