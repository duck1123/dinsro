(ns dinsro.components.user
  (:require [ajax.core :as ajax]
            [re-frame.core :refer [subscribe dispatch]]
            [reagent.core :as r]))

(defn page
  []
  [:section.section>div.container>div.content
   [:h1 "Show User"]])
