(ns dinsro.ui.debug-menu
  (:require
   [clojure.string :as string]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.routing :as routing]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [taoensso.timbre :as log]))

(defsc DebugLinkButton
  [_this {:navlink/keys [path name]}]
  {:query         [:navlink/id
                   :navlink/path
                   :navlink/name]
   :ident         :navlink/id
   :initial-state {:navlink/id   :unset
                   :navlink/path ["unset"]
                   :navlink/name "Unset"}}
  (dom/button :.button
    {:onClick #(routing/route-to! (str "/" (string/join "/" path)))} name))

(def ui-debug-link-button (comp/factory DebugLinkButton {:keyfn :navlink/id}))

(defsc DebugLinkBar
  [_this {:keys [items]}]
  {:componentDidMount
   (fn [this]
     (df/load! this :debug-menu/list DebugLinkButton
               {:target [:component/id ::DebugLinkBar :items]}))
   :query         [{:items (comp/get-query DebugLinkButton)}]
   :ident         (fn [_] [:component/id ::DebugLinkBar])
   :initial-state {:items []}}
  (bulma/container
   (bulma/box
    (map ui-debug-link-button items))))

(def ui-debug-link-bar (comp/factory DebugLinkBar))
