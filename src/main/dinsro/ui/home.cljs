(ns dinsro.ui.home
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.routing :as rroute]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.login :as v.login]
   [taoensso.timbre :as log]))

(defn path-for
  [_p]
  "/login")

(defsc HomePage
  [this {:keys [auth-id]}]
  {:ident         (fn [] [:page/id ::page])
   :initial-state {:page/id ::page}
   :query         [:auth-id :page/id]
   :route-segment [""]}
  (bulma/page
   (if auth-id
     (dom/div "Authenticated")
     (bulma/box
      (dom/h1 :.title (tr [:home-page]))
      (dom/p {}
        "Not Authenticated. "
        (dom/a :.login-link
          {:onClick (fn [] (rroute/route-to! this v.login/LoginPage {}))}
          "login"))))))

(def ui-page (comp/factory HomePage))
