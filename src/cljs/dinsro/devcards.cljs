(ns dinsro.devcards
  (:require
   ;; [example.core-test]
   ;; [example.another-core-test]
   [reagent.core :refer [as-element]]
   [devcards.core :refer-macros [defcard-rg]]
   [devcards.core :refer [start-devcard-ui!]]))

(defn about-page [name]
  (fn []
    [:span.main
     [:h1 "About hello-devcar2: " @name]]))


(defcard-rg hello-user-test
  "**Documentation**"
  (fn [name] [about-page name])
  {:name "foo"}
  )

(js/console.log "starting ui")
(start-devcard-ui!)
