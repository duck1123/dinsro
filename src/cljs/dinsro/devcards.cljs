(ns dinsro.devcards
  (:require [devcards.core
             :refer [start-devcard-ui!]
             :refer-macros [defcard-rg]]
            [dinsro.app]))

(defn about-page [name]
  (fn []
    [:span.main
     [:h1 "About hello-devcar2: " @name]]))

(defcard-rg hello-user-test
  "**Documentation**"
  (fn [name] [about-page name])
  {:name "foo"})

(defn start
  []
  (start-devcard-ui!))
