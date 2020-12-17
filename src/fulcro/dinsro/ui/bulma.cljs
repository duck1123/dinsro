(ns dinsro.ui.bulma
  (:require
   [com.fulcrologic.fulcro.dom :as dom]))

(defn box
  [& body]
  (apply dom/div :.box body))

(defn column
  [& body]
  (apply dom/div :.column body))

(defn container
  [& body]
  (apply dom/div :.container body))

(defn content
  [& body]
  (apply dom/div :.content body))

(defn control
  [& body]
  (apply dom/div :.control body))

(defn field-group
  [& body]
  (apply dom/div :.field-group body))

(defn field
  [& body]
  (apply dom/div :.field body))

(defn section
  [& body]
  (apply dom/section :.section body))
