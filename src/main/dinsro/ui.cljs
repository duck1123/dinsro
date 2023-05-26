(ns dinsro.ui
  (:require
   ["fomantic-ui"]
   [clojure.string :as string]
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro-css.css-injection :as inj]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [com.fulcrologic.rad.authorization :as auth]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid :refer [ui-grid]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid-column :refer [ui-grid-column]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid-row :refer [ui-grid-row]]
   [com.fulcrologic.semantic-ui.collections.message.ui-message :refer [ui-message]]
   [com.fulcrologic.semantic-ui.elements.container.ui-container :refer [ui-container]]
   [com.fulcrologic.semantic-ui.modules.sidebar.ui-sidebar-pushable :refer [ui-sidebar-pushable]]
   [com.fulcrologic.semantic-ui.modules.sidebar.ui-sidebar-pusher :refer [ui-sidebar-pusher]]
   [dinsro.machines :as machines]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.settings :as m.settings]
   [dinsro.mutations.navbars :as mu.navbars]
   [dinsro.mutations.ui :as mu.ui]
   [dinsro.ui.accounts :as u.accounts]
   [dinsro.ui.admin :as u.admin]
   [dinsro.ui.authenticator :as u.authenticator]
   [dinsro.ui.categories :as u.categories]
   [dinsro.ui.contacts :as u.contacts]
   [dinsro.ui.core :as u.core]
   [dinsro.ui.currencies :as u.currencies]
   [dinsro.ui.debits :as u.debits]
   [dinsro.ui.home :as u.home]
   [dinsro.ui.initialize :as u.initialize]
   [dinsro.ui.ln :as u.ln]
   [dinsro.ui.login :as u.login]
   [dinsro.ui.navbars :as u.navbars]
   [dinsro.ui.nodes :as u.nodes]
   [dinsro.ui.nostr :as u.nostr]
   [dinsro.ui.registration :as u.registration]
   [dinsro.ui.settings :as u.settings]
   [dinsro.ui.transactions :as u.transactions]
   [lambdaisland.glogc :as log]))

(defsc GlobalErrorDisplay [this {:ui/keys [global-error]}]
  {:query         [[:ui/global-error '_]]
   :ident         (fn [] [:component/id :GlobalErrorDisplay])
   :initial-state {}}
  (when global-error
    (ui-message
     {:content   (str "Something went wrong: " global-error)
      :error     true
      :onDismiss #(comp/transact!! this [(mu.ui/reset-global-error {})])})))

(def ui-global-error-display (comp/factory GlobalErrorDisplay))

(defrouter RootRouter
  [_this {:keys [current-state route-factory route-props]}]
  {:css            [[:.root-router {:height "100%"}]]
   :router-targets [u.accounts/Report
                    u.accounts/NewForm
                    u.accounts/Show
                    u.admin/AdminPage
                    u.categories/Report
                    u.categories/NewForm
                    u.contacts/NewContactForm
                    u.contacts/Report
                    u.core/CorePage
                    u.currencies/Report
                    u.currencies/NewForm
                    u.currencies/Show
                    u.debits/Show
                    u.home/Page
                    u.login/LoginPage
                    u.ln/Page
                    u.nodes/Page
                    u.nostr/Page
                    u.registration/RegistrationPage
                    u.settings/SettingsPage
                    u.transactions/NewTransaction
                    u.transactions/Show
                    u.transactions/Report]}
  (let [{:keys [root-router]} (css/get-classnames RootRouter)]
    (case current-state
      :pending (dom/div "Loading...")
      :failed  (dom/div "Failed!")
      ;; default will be used when the current state isn't yet set
      (dom/div {:classes [root-router]}
        (dom/div "No route selected.")
        (when route-factory
          (comp/fragment
           (route-factory route-props)))))))

(def ui-root-router (comp/factory RootRouter))

(defsc Menus
  [_this _props]
  {:query
   [{:root/main-nav (comp/get-query u.navbars/Navbar)}
    {:root/sidebar-nav (comp/get-query u.navbars/NavbarSidebar)}
    {:root/unauth-nav (comp/get-query u.navbars/Navbar)}]
   :initial-status
   {:root/main-nav    {}
    :root/sidebar-nav {}
    :root/unauth-nav  {}}})

(defsc Config
  [_this _props]
  {:ident         ::m.settings/id
   :query         [::m.settings/id
                   ::m.settings/initialized?
                   ::m.settings/loaded?
                   {::m.settings/auth (comp/get-query auth/Session)}
                   {::m.settings/menu (comp/get-query u.navbars/Navbar)}]
   :initial-state {::m.settings/id           :main
                   ::m.settings/initialized? false
                   ::m.settings/loaded?      false
                   ::m.settings/auth         {}
                   ::m.settings/menu         {}}})

(defsc Root
  [this {:root/keys        [authenticator global-error init-form]
         :ui/keys          [router]
         ::m.settings/keys [site-config]
         :as               props}]
  {:componentDidMount
   (fn [this]
     (log/trace :Root/mounted {:this this})
     (df/load! this ::m.settings/site-config Config)
     (uism/begin! this machines/hideable ::mu.navbars/navbarsm
                  {:actor/navbar (uism/with-actor-class [::m.navbars/id :main] u.navbars/Navbar)}))
   :css           [[:.primary-grid {:height "100%" :overflow "auto"}]
                   [:.pushed {:height "100%" :margin-top "40px"}]
                   [:.pusher {:height "100%"}]
                   [:.root-container {:height "100%"}]
                   [:.router-wrapper {:overflow "hidden" :height "100%"}]]
   :query         [{[::auth/authorization :local] (comp/get-query u.navbars/NavbarAuthQuery)}
                   {::m.settings/site-config (comp/get-query Config)}
                   {:root/authenticator (comp/get-query u.authenticator/Authenticator)}
                   {:root/global-error (comp/get-query GlobalErrorDisplay)}
                   {:root/init-form (comp/get-query u.initialize/InitForm)}
                   {:ui/router (comp/get-query RootRouter)}]
   :initial-state {::m.settings/site-config {}
                   :root/authenticator      {}
                   :root/global-error       {}
                   :root/init-form          {}
                   :ui/router               {}}}
  (log/trace :Root/starting {:props props})
  (let [navbar                                     (::m.settings/menu site-config)
        {:keys [primary-grid pushed pusher
                root-container router-wrapper]}    (css/get-classnames Root)
        top-router-state                           (or (uism/get-active-state this ::RootRouter) :initial)
        {::m.settings/keys [loaded? initialized?]} site-config
        root                                       (uism/get-active-state this ::auth/auth-machine)
        gathering-credentials?                     (#{:state/gathering-credentials} root)
        authenticated?                             (not= (get-in props [[::auth/authorization :local] ::auth/status]) :not-logged-in)]
    (dom/div {:classes [:.ui root-container]}
      (if loaded?
        (if initialized?
          (comp/fragment
           (ui-grid {}
             (ui-grid-row {:only "computer tablet"}
               (ui-grid-column {:width 16}
                 (ui-container {:fluid true}
                   (u.navbars/ui-navbar navbar))))
             (ui-grid-row {:only "mobile"}
               (ui-grid-column {:width 16}
                 (ui-container {:fluid true}
                   (if authenticated?
                     (u.navbars/ui-minimal-navbar navbar)
                     (u.navbars/ui-navbar navbar))))))
           (ui-sidebar-pushable {}
             (u.navbars/ui-navbar-sidebar navbar)
             (ui-sidebar-pusher {:className (string/join " " [pusher])}
               (dom/div {:className (string/join " " [pushed])}
                 (ui-grid {:className (string/join "" [primary-grid])}
                   (ui-grid-row {:centered true}
                     (ui-grid-column {}
                       (if (= :initial top-router-state)
                         (dom/div :.loading "Loading...")
                         (comp/fragment
                          (ui-global-error-display global-error)
                          (u.authenticator/ui-authenticator authenticator)
                          (when-not gathering-credentials?
                            (dom/div {:classes [router-wrapper]}
                              (ui-root-router router))))))))))))
          (u.initialize/ui-init-form init-form))
        (dom/div :.ui.segment
          (dom/p "Not loaded")))
      (inj/style-element {:component Root}))))
