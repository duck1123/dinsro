(ns dinsro.views.admin
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [taoensso.timbre :as timbre]))

(defsc AdminPage
  [_this _props]
  {:query []}
  (dom/div "admin page"))
