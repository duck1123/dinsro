(ns dinsro.components.index-users)

(defn index-users
  []
  (let [users []]
    [:div
     [:p (count users)]]))

(defn page
  []
  [:div
   [:h1 "Users Page"]
   [index-users]])
