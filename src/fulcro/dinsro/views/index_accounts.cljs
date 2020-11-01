(ns dinsro.views.index-accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [taoensso.timbre :as timbre]))

(defsc IndexAccountsPage
  [_this _props]
  (dom/section
   :.section
   (dom/div
    :.container
    (dom/div
     :.content
     (dom/p "Index Accounts")))))

(def ui-page (comp/factory IndexAccountsPage))
