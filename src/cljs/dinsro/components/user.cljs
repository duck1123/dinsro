(ns dinsro.components.user)

(defn index-users
  [users]
  [:div
   ;; [list-user-accounts {:id id}]
   [:ul
    (map
     (fn [user]
       [:li
        [:p (:id user) " - " (:name user)]])
     users)]])
