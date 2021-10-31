(ns dinsro.ui
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [com.fulcrologic.rad.authorization :as auth]
   [com.fulcrologic.semantic-ui.modules.sidebar.ui-sidebar-pushable :refer [ui-sidebar-pushable]]
   [com.fulcrologic.semantic-ui.modules.sidebar.ui-sidebar-pusher :refer [ui-sidebar-pusher]]
   [dinsro.machines :as machines]
   [dinsro.model.navlink :as m.navlink]
   [dinsro.ui.accounts :as u.accounts]
   [dinsro.ui.admin :as u.admin]
   [dinsro.ui.authenticator :as u.authenticator]
   [dinsro.ui.categories :as u.categories]
   [dinsro.ui.currencies :as u.currrencies]
   [dinsro.ui.home :as u.home]
   [dinsro.ui.login :as u.login]
   [dinsro.ui.media :as u.media]
   [dinsro.ui.navbar :as u.navbar]
   [dinsro.ui.rates :as u.rates]
   [dinsro.ui.rate-sources :as u.rate-sources]
   [dinsro.ui.registration :as u.registration]
   [dinsro.ui.transactions :as u.transactions]
   [dinsro.ui.users :as u.users]
   [taoensso.timbre :as log]))

(defrouter RootRouter
  [_this {:keys [current-state]}]
  {:router-targets [u.accounts/AccountForm
                    u.accounts/AccountsReport
                    u.admin/AdminPage
                    u.categories/CategoryForm
                    u.categories/CategoriesReport
                    u.currrencies/CurrencyForm
                    u.currrencies/CurrenciesReport
                    u.home/HomePage
                    u.login/LoginPage
                    u.registration/RegistrationPage
                    u.rate-sources/RateSourceForm
                    u.rate-sources/RateSourcesReport
                    u.rates/RateForm
                    u.rates/RatesReport
                    u.transactions/TransactionForm
                    u.transactions/TransactionsReport
                    u.users/UserForm
                    u.users/UsersReport]}
  (case current-state
    :pending (dom/div "Loading...")
    :failed  (dom/div "Failed!")
    ;; default will be used when the current state isn't yet set
    (dom/div "No route selected.")))

(def ui-root-router (comp/factory RootRouter))

(defsc NavbarUnion
  [_this _props]
  {:query         [:navbar/id
                   {::navbar (comp/get-query u.navbar/Navbar)}
                   {::sidebar (comp/get-query u.navbar/NavbarSidebar)}]
   :initial-state {:navbar/id :main
                   ::navbar   {}
                   ::sidebar  {}}})

(defsc Root [this {:root/keys [navbar sidebar]
                   ::keys     [router]}]
  {:componentDidMount
   (fn [this]
     (uism/begin! this machines/hideable ::u.navbar/navbarsm
                  {:actor/navbar (uism/with-actor-class [:navbar/id :main] u.navbar/Navbar)})
     (df/load! this ::m.navlink/current-navbar u.navbar/Navbar
               {:target [:root/navbar]})
     (df/load! this ::m.navlink/current-navbar u.navbar/NavbarSidebar
               {:target [:root/sidebar]}))
   :query
   [{:authenticator (comp/get-query u.authenticator/Authenticator)}

    {:root/navbar (comp/get-query u.navbar/Navbar)}
    {:root/sidebar (comp/get-query u.navbar/NavbarSidebar)}
    {::router (comp/get-query RootRouter)}
    ::auth/authorization]
   :initial-state {:root/navbar   {}
                   :root/sidebar  {}
                   :authenticator {}
                   ::router       {}}}
  (let [inverted         true
        visible          (= (uism/get-active-state this ::u.navbar/navbarsm) :state/shown)
        top-router-state (or (uism/get-active-state this ::RootRouter) :initial)]
    (comp/fragment
     (u.media/ui-media-styles)
     (u.media/ui-media-context-provider
      {}
      (dom/div {:className "ui container"
                :style     {:height "100%"}}
        (when navbar
          (u.navbar/ui-navbar navbar))
        (ui-sidebar-pushable
         {:inverted (str inverted)
          :visible  (str visible)}
         (when sidebar
           (u.navbar/ui-navbar-sidebar sidebar))
         (ui-sidebar-pusher
          {}
          (if (= :initial top-router-state)
            (dom/div :.loading "Loading...")
            (ui-root-router router)))))))))

(def ui-root (comp/factory Root))
