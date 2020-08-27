(ns dinsro.views.about)

(defn page [_store _match]
  [:section.section>div.container>div.content
   [:h1 "About"]
   [:img {:src "/img/warning_clojure.png"}]])
