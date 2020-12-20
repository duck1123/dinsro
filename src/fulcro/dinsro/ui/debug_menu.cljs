(ns dinsro.ui.debug-menu
  (:require
   [clojure.string :as string]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.routing :as routing]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [taoensso.timbre :as timbre]))

(defsc DebugLinkButton
  [_this {:debug-menu/keys [path label]}]
  {:query [:debug-menu/id
           :debug-menu/path
           :debug-menu/label]
   :ident :debug-menu/id
   :initial-state {}}
  (dom/button
   :.button
   {:onClick #(routing/route-to! (str "/" (string/join "/" path)))} label))

(def ui-debug-link-button (comp/factory DebugLinkButton {:keyfn :debug-menu/id}))

(defsc DebugLinkBar
  [_this {:keys [items]}]
  {:query [{:items (comp/get-query DebugLinkButton)}]
   :ident (fn [_] [:component/id ::component])
   :initial-state {:items []}}
  (bulma/container
   (bulma/box
    (map ui-debug-link-button items))))

(def ui-debug-link-bar (comp/factory DebugLinkBar))
