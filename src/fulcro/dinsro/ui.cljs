(ns dinsro.ui
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [dinsro.router :as router]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.debug-menu :as u.debug-menu]
   [dinsro.ui.navbar :as u.navbar]
   [taoensso.timbre :as timbre]))

(defsc Root
  [this {::keys [debug-links navbar router]}]
  {:query [{::debug-links (comp/get-query u.debug-menu/DebugLinkBar)}
           {::navbar (comp/get-query u.navbar/Navbar)}
           {::router (comp/get-query router/RootRouter)}]
   :initial-state {::debug-links {}
                   ::navbar {}
                   ::router {}}}
  (let [top-router-state (or (uism/get-active-state this ::router/RootRouter) :initial)]
    (dom/div
     (u.navbar/ui-navbar navbar)
     (u.debug-menu/ui-debug-link-bar debug-links)
     (bulma/container
      (if (= :initial top-router-state)
        (dom/div :.loading "Loading...")
        (router/ui-root-router router))))))
