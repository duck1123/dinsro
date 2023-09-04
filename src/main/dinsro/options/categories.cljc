(ns dinsro.options.categories
  (:refer-clojure :exclude [name])
  (:require
   [dinsro.model.categories :as m.categories]))

;; [[../model/categories.cljc]]

(def id
  "The id of the category"
  ::m.categories/id)

(def name
  "The name of the category"
  ::m.categories/name)

(def user
  "The user this category belongs to"
  ::m.categories/user)
