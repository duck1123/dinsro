(ns dinsro.views.home
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defn path-for
  [_p]
  "")

(defsc HomePage
  [_this {:keys [auth-id]}]
  {:query [:auth-id :page-name]
   :route-segment [""]
   :ident (fn [_] [:page-name :home-page])}
  (dom/section
   :.section
   (dom/div
    :.container
    (dom/div
     :.content
     (if auth-id
       (dom/div "Authenticated")
       (dom/div
        :.box
        (dom/h1 :.title (tr [:home-page]))
        (dom/p
         "Not Authenticated. "
         (dom/a {:href (path-for [:login-page])} "login"))))))))

(def ui-page (comp/factory HomePage))
