(ns dinsro.views.index-currencies
  (:require [dinsro.components.index-currencies :refer [index-currencies]]
            [taoensso.timbre :as timbre]))

(defn page
  []
  [:section.section>div.container>div.content
   [:h1 "Index Currencies"]
   [index-currencies]])
