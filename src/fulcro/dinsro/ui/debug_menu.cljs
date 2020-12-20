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
  (dom/button {:onClick #(dr/change-route this path)} label))

(def ui-debug-link-button (comp/factory DebugLinkButton {:keyfn :debug-menu/id}))

(defsc DebugLinkBar
  [_this {:keys [all-debug-menus]}]
  {:query [{:all-debug-menus (comp/get-query DebugLinkButton)}]
   :initial-state
   (fn [_]
     :all-debug-menus [(comp/get-initial-state DebugLinkButton)])}
  (bulma/container
   (bulma/box
    (map ui-debug-link-button all-debug-menus))))

(def ui-debug-link-bar (comp/factory DebugLinkBar))
