(ns dinsro.ui
  (:require
   [com.fulcrologic.fulcro.application :as app]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.dom.html-entities :as ent]
   [com.fulcrologic.fulcro.routing.dynamic-routing :refer [defrouter]]
   [com.fulcrologic.rad.authorization :as auth]
   [com.fulcrologic.rad.routing :as rroute]
   #?(:cljs [com.fulcrologic.semantic-ui.modules.dropdown.ui-dropdown :refer [ui-dropdown]])
   #?(:cljs [com.fulcrologic.semantic-ui.modules.dropdown.ui-dropdown-item :refer [ui-dropdown-item]])
   #?(:cljs [com.fulcrologic.semantic-ui.modules.dropdown.ui-dropdown-menu :refer [ui-dropdown-menu]])
   [dinsro.ui.login-dialog :refer [LoginForm]]
   [taoensso.timbre :as log]))

(defsc LandingPage
  [_this _props]
  {:query         ['*]
   :ident         (fn [] [:component/id ::LandingPage])
   :initial-state {}
   :route-segment ["landing-page"]}
  (dom/div "Welcome to the Demo. Please log in."))

;; This will just be a normal router...but there can be many of them.
(defrouter MainRouter
  [_this {:keys [current-state route-factory route-props]}]
  {:always-render-body? true
   :router-targets      [LandingPage]}
  (dom/div {}
    (dom/div :.ui.loader
      {:classes [(when-not (= :routed current-state) "active")]})
    (when route-factory
      (route-factory route-props))))

(def ui-main-router (comp/factory MainRouter))

(auth/defauthenticator Authenticator {:local LoginForm})

(def ui-authenticator (comp/factory Authenticator))

(defsc Root
  [this {::auth/keys [authorization]
         ::app/keys  [active-remotes]
         :keys       [authenticator router]}]
  {:query         [{:authenticator (comp/get-query Authenticator)}
                   {:router (comp/get-query MainRouter)}
                   ::app/active-remotes
                   ::auth/authorization]
   :initial-state {:router        {}
                   :authenticator {}}}
  (let [logged-in? (= :success (some-> authorization :local ::auth/status))
        busy?      (seq active-remotes)
        username   (some-> authorization :local :account/name)]
    (dom/div {}
      (dom/div :.ui.top.menu
        (dom/div :.ui.item "Demo")
        (when logged-in?
          #?(:clj
             (comp/fragment)
             :cljs
             (comp/fragment
              (ui-dropdown
               {:className "item" :text "Account"}
               (ui-dropdown-menu
                {}
                (ui-dropdown-item {:onClick (fn [])} "View All")
                (ui-dropdown-item {:onClick (fn [])} "New"))))))
        (dom/div :.right.menu
          (dom/div :.item
            (dom/div :.ui.tiny.loader {:classes [(when busy? "active")]})
            ent/nbsp ent/nbsp ent/nbsp ent/nbsp)
          (if logged-in?
            (comp/fragment
             (dom/div :.ui.item
               (str "Logged in as " username))
             (dom/div :.ui.item
               (dom/button :.ui.button
                 {:onClick (fn []
                             ;; TODO: check if we can change routes...
                             (rroute/route-to! this LandingPage {})
                             (auth/logout! this :local))}
                 "Logout")))
            (dom/div :.ui.item
              (dom/button :.ui.primary.button {:onClick #(auth/authenticate! this :local nil)}
                          "Login")))))
      (dom/div :.ui.container.segment
        (ui-authenticator authenticator)
        (ui-main-router router)))))

(def ui-root (comp/factory Root))
