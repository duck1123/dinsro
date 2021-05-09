(ns dinsro.views.admin-index-accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as log]))

(defsc AdminIndexAccountsPage
  [_this _props]
  (dom/div "admin index accounts page"))
