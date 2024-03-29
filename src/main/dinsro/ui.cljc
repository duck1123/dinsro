(ns dinsro.ui
  (:require
   #?(:cljs ["fomantic-ui"])
   [clojure.string :as string]
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro-css.css-injection :as inj]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [com.fulcrologic.rad.authorization :as auth]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid :refer [ui-grid]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid-column :refer [ui-grid-column]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid-row :refer [ui-grid-row]]
   [com.fulcrologic.semantic-ui.collections.message.ui-message :refer [ui-message]]
   [com.fulcrologic.semantic-ui.elements.container.ui-container :refer [ui-container]]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [com.fulcrologic.semantic-ui.modules.sidebar.ui-sidebar-pushable :refer [ui-sidebar-pushable]]
   [com.fulcrologic.semantic-ui.modules.sidebar.ui-sidebar-pusher :refer [ui-sidebar-pusher]]
   [dinsro.machines :as machines]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.settings :as m.settings]
   [dinsro.mutations.navbars :as mu.navbars]
   [dinsro.mutations.ui :as mu.ui]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.accounts :as u.accounts]
   [dinsro.ui.admin :as u.admin]
   [dinsro.ui.authenticator :as u.authenticator]
   [dinsro.ui.breadcrumbs :as u.breadcrumbs]
   [dinsro.ui.categories :as u.categories]
   [dinsro.ui.contacts :as u.contacts]
   [dinsro.ui.core :as u.core]
   [dinsro.ui.currencies :as u.currencies]
   [dinsro.ui.debits :as u.debits]
   [dinsro.ui.forms.categories :as u.f.categories]
   [dinsro.ui.forms.contacts :as u.f.contacts]
   [dinsro.ui.forms.currencies :as u.f.currencies]
   [dinsro.ui.forms.transactions :as u.f.transactions]
   [dinsro.ui.home :as u.home]
   [dinsro.ui.initialize :as u.initialize]
   [dinsro.ui.ln :as u.ln]
   [dinsro.ui.login :as u.login]
   [dinsro.ui.navbars :as u.navbars]
   [dinsro.ui.navlinks :as u.navlinks]
   [dinsro.ui.nostr :as u.nostr]
   [dinsro.ui.registration :as u.registration]
   [dinsro.ui.settings :as u.settings]
   [dinsro.ui.transactions :as u.transactions]
   [lambdaisland.glogc :as log]))

;; [[./ui/admin.cljc]]

(def show-page-id :root)
(def show-breadcrumbs true)

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
   :router-targets [u.accounts/IndexPage
                    ;; u.accounts/NewForm
                    u.accounts/ShowPage
                    u.admin/IndexPage
                    u.categories/IndexPage
                    u.f.categories/NewForm
                    u.categories/ShowPage
                    u.f.contacts/NewContactForm
                    u.contacts/ShowPage
                    u.contacts/IndexPage
                    u.core/IndexPage
                    u.currencies/IndexPage
                    u.f.currencies/NewForm
                    u.currencies/ShowPage
                    u.debits/ShowPage
                    u.home/IndexPage
                    u.login/IndexPage
                    u.ln/IndexPage
                    u.navbars/IndexPage
                    u.navlinks/IndexPage
                    u.nostr/IndexPage
                    u.registration/IndexPage
                    u.settings/IndexPage
                    u.f.transactions/NewTransaction
                    u.transactions/ShowPage
                    u.transactions/IndexPage]}
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

(m.navbars/defmenu show-page-id
  {::m.navbars/parent nil
   ::m.navbars/router ::RootRouter
   ::m.navbars/children
   [u.accounts/index-page-id
    u.admin/index-page-id
    u.contacts/index-page-id
    u.currencies/index-page-id
    :home
    :login
    :navbars
    :navlinks
    :nostr
    :nodes
    :registration
    :settings
    :transactions]})

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

(defsc BreadcrumbsLinkGrid
  [_this {:ui/keys [breadcrumbs]}]
  {:initial-state {:ui/breadcrumbs {}}
   :query         [{:ui/breadcrumbs (comp/get-query u.breadcrumbs/Breadcrumbs)}]}
  (ui-grid {}
    (ui-grid-row {}
      (ui-grid-column {:width 16}
        (ui-container {:fluid true}
          (ui-segment {}
            (u.breadcrumbs/ui-breadcrumbs breadcrumbs)))))))

(def ui-breadcrumbs-link-grid (comp/factory BreadcrumbsLinkGrid))

(defsc Root
  [this {:root/keys        [authenticator global-error init-form]
         :ui/keys          [breadcrumbs-grid router]
         ::m.settings/keys [site-config]
         :as               props}]
  {:componentDidMount (fn [this]
                        (log/trace :Root/mounted {:this this})
                        (df/load! this ::m.settings/site-config Config)
                        (uism/begin! this machines/hideable ::mu.navbars/navbarsm
                                     {:actor/navbar (uism/with-actor-class [::m.navbars/id :main] u.navbars/Navbar)}))
   :css               [[:.primary-grid {:height "100%" :overflow "auto"}]
                       [:.primary-row {:margin-bottom "100px"}]
                       [:.pushed {:height "100%" :margin-top "40px"}]
                       [:.pusher {:height "100%"}]
                       [:.root-container {:height "100%"}]
                       [:.router-wrapper {:overflow "hidden" :height "100%"}]]
   :query             [{[::auth/authorization :local] (comp/get-query u.navbars/NavbarAuthQuery)}
                       {::m.settings/site-config (comp/get-query Config)}
                       {:root/authenticator (comp/get-query u.authenticator/Authenticator)}
                       {:root/global-error (comp/get-query GlobalErrorDisplay)}
                       {:root/init-form (comp/get-query u.initialize/InitForm)}
                       {:ui/breadcrumbs-grid (comp/get-query BreadcrumbsLinkGrid)}
                       {:ui/router (comp/get-query RootRouter)}]
   :initial-state     {::m.settings/site-config {}
                       :root/authenticator      {}
                       :root/global-error       {}
                       :root/init-form          {}
                       :ui/breadcrumbs-grid     {}
                       :ui/router               {}}}
  (log/trace :Root/starting {:props props})
  (let [navbar                                     (::m.settings/menu site-config)
        {:keys [primary-grid primary-row
                pushed pusher root-container
                router-wrapper]}                   (css/get-classnames Root)
        top-router-state                           (or (uism/get-active-state this ::RootRouter) :initial)
        {::m.settings/keys [loaded? initialized?]} site-config
        root                                       (uism/get-active-state this ::auth/auth-machine)
        gathering-credentials?                     (#{:state/gathering-credentials} root)
        authenticated?                             (not= (get-in props [[::auth/authorization :local] ::auth/status]) :not-logged-in)]
    (dom/div {:classes [:.ui root-container]}
      (if loaded?
        (if initialized?
          (dom/div {}
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
                  (dom/div {}
                    (when show-breadcrumbs (ui-breadcrumbs-link-grid breadcrumbs-grid)))
                  (dom/div {}
                    (ui-grid {:className (string/join "" [primary-grid])}
                      (ui-grid-row {:centered  true
                                    :className primary-row}
                        (ui-grid-column {}
                          (if (= :initial top-router-state)
                            (dom/div :.loading "Loading...")
                            (comp/fragment
                             (ui-global-error-display global-error)
                             (u.authenticator/ui-authenticator authenticator)
                             (when-not gathering-credentials?
                               (dom/div {:classes [router-wrapper]}
                                 (if router
                                   (ui-root-router router)
                                   (ui-segment {}
                                     "Failed to load router"))))))))))))))
          (u.initialize/ui-init-form init-form))
        (ui-segment {}
          (dom/p "Not loaded")))
      (inj/style-element {:component Root}))))

(m.navlinks/defroute :root
  {o.navlinks/control       ::Page
   o.navlinks/label         "Home"
   o.navlinks/parent-key    nil
   o.navlinks/required-role :guest})
