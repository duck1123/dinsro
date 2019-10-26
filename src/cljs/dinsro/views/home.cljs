(ns dinsro.views.home)

(defn page
  []
  (let [strings {:title "Home Page"}]
    [:section.section>div.container>div.content
     [:h1 (:title strings)]]))
