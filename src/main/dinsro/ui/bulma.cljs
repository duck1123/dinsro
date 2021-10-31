(ns dinsro.ui.bulma
  (:require
   [com.fulcrologic.fulcro.dom :as dom]))

(defn box
  [& body]
  (apply dom/div :.box body))

(defn container
  [& body]
  (apply dom/div :.container body))

(defn content
  [& body]
  (apply dom/div :.content body))

(defn control
  [& body]
  (apply dom/div :.control body))

(defn field
  [& body]
  (apply dom/div :.field body))

(defn section
  [& body]
  (apply dom/section :.section body))

(defn page
  [& body]
  (section
   (container
    (apply content body))))
