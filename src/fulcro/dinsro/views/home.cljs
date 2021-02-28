(ns dinsro.views.home
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [taoensso.timbre :as timbre]))

(defn path-for
  [_p]
  "/login")

(defsc HomePage
  [_this {:keys [auth-id]}]
  {:ident (fn [] [:page/id ::page])
   :query [:auth-id :page/id]
   :route-segment [""]}
  (bulma/page
   (if auth-id
     (dom/div "Authenticated")
     (bulma/box
      (dom/h1 :.title (tr [:home-page]))
      (dom/p
       "Not Authenticated. "
       (dom/a :.login-link {:href (path-for [:login-page])} "login"))))))

(def ui-page (comp/factory HomePage))
