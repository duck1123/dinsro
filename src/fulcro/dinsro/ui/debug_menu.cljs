(ns dinsro.ui.debug-menu
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [taoensso.timbre :as timbre]))

(defsc DebugLinkButton
  [this {:debug-menu/keys [path label]}]
  {:query [:debug-menu/id
           :debug-menu/path
           :debug-menu/label]
   :ident :debug-menu/id
   :initial-state {}}
  (dom/button
   :.button
   {:onClick #(dr/change-route this path)} label))

(def ui-debug-link-button (comp/factory DebugLinkButton {:keyfn :debug-menu/id}))

(defsc DebugLinkBar
  [_this {:keys [items]}]
  {:query [{:items (comp/get-query DebugLinkButton)}]
   :initial-state {:items []}}
  (bulma/container
   (bulma/box
    (map ui-debug-link-button items))))

(def ui-debug-link-bar (comp/factory DebugLinkBar))
