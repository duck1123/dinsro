(ns dinsro.views.home
  (:require [dinsro.translations :refer [tr]]))

(defn page
  []
  (let [strings {:title "Home Page"}]
    [:section.section>div.container>div.content
     [:h1 (:title strings)]]))
