(ns dinsro.views.home
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [taoensso.timbre :as timbre]))

(defn path-for
  [_p]
  "")

(defsc HomePage
  [_this {:keys [auth-id]}]
  {:query [:auth-id :page/id]
   :route-segment [""]
   :ident (fn [] [:page/id ::page])}
  (bulma/section
   (bulma/container
    (bulma/content
     (if auth-id
       (dom/div "Authenticated")
       (bulma/box
        (dom/h1 :.title (tr [:home-page]))
        (dom/p
         "Not Authenticated. "
         (dom/a {:href (path-for [:login-page])} "login"))))))))

(def ui-page (comp/factory HomePage))
