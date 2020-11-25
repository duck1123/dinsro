(ns dinsro.routing
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [dinsro.views.home :as v.home]

   [dinsro.views.login :as v.login]))

(defrouter RootRouter
  [_this _props]
  {:router-targets [v.home/HomePage
                    v.login/LoginPage]}
  (dom/div "No route selected"))

(def ui-root-router (comp/factory RootRouter))
