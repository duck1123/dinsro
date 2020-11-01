(ns dinsro.ui.admin-index-users
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.index-users :as u.index-users]
   [taoensso.timbre :as timbre]))

(defsc AdminIndexUsers
  [_this _props]
  (let [users []]
    (dom/div
     :.box
     (dom/h2 (tr [:users]))
     (u.index-users/ui-index-users users))))
