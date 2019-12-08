(ns dinsro.views.about
  (:require [dinsro.translations :refer [tr]]))

(defn page []
  [:section.section>div.container>div.content
   [:h1 "About"]
   [:img {:src "/img/warning_clojure.png"}]])
