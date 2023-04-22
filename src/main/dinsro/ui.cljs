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
   [com.fulcrologic.semantic-ui.collections.message.ui-message :refer [ui-message]]
   [com.fulcrologic.semantic-ui.modules.sidebar.ui-sidebar-pushable :refer [ui-sidebar-pushable]]
   [com.fulcrologic.semantic-ui.modules.sidebar.ui-sidebar-pusher :refer [ui-sidebar-pusher]]
   [dinsro.machines :as machines]
   [dinsro.model.navbar :as m.navbar]
   [dinsro.model.settings :as m.settings]
   [dinsro.mutations.navbar :as mu.navbar]
   [dinsro.mutations.settings :as mu.settings]
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
   [dinsro.ui.navbar :as u.navbar]
   [dinsro.ui.nostr :as u.nostr]
   [dinsro.ui.rate-sources :as u.rate-sources]
   [dinsro.ui.registration :as u.registration]
   [dinsro.ui.settings :as u.settings]
   [dinsro.ui.transactions :as u.transactions]
   [dinsro.ui.users :as u.users]))

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
  {:css            [[:.rootrouter {:height "100%"
                                   :border "1px solid pink"}]]
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
                    u.ln/LnPage
                    u.nostr/Page
                    u.rate-sources/Show
                    u.rate-sources/Report
                    u.registration/RegistrationPage
                    u.settings/SettingsPage
                    u.transactions/NewForm
                    u.transactions/Show
                    u.transactions/Report
                    u.users/Show
                    u.users/Report]}
  (let [{:keys [rootrouter]} (css/get-classnames RootRouter)]
    (case current-state
      :pending (dom/div "Loading...")
      :failed  (dom/div "Failed!")
      ;; default will be used when the current state isn't yet set
      (dom/div {:classes [rootrouter]}
        (dom/div "No route selected.")
        (when route-factory
          (comp/fragment
           (route-factory route-props)))))))

(def ui-root-router (comp/factory RootRouter))

(defsc Root
  [this {:root/keys        [authenticator global-error init-form navbar]
         :ui/keys          [router]
         ::m.settings/keys [site-config]}]
  {:componentDidMount (fn [this]
                        (df/load! this ::m.settings/site-config mu.settings/Config)
                        (df/load! this :root/navbar u.navbar/NavbarUnion)
                        (uism/begin! this machines/hideable ::mu.navbar/navbarsm
                                     {:actor/navbar (uism/with-actor-class [::m.navbar/id :main] u.navbar/Navbar)}))
   :css               [[:.container {:height "100%" :overflow "hidden"}]
                       [:.pusher {:height "100%" :overflow "auto !important"}]
                       [:.top {:height "100%"}]]
   :query             [{:root/authenticator (comp/get-query u.authenticator/Authenticator)}
                       {:root/navbar (comp/get-query u.navbar/Navbar)}
                       {:root/init-form (comp/get-query u.initialize/InitForm)}
                       {:root/global-error (comp/get-query GlobalErrorDisplay)}
                       {:ui/router (comp/get-query RootRouter)}
                       ::auth/authorization
                       {::m.settings/site-config (comp/get-query mu.settings/Config)}]
   :initial-state     {:root/navbar             {}
                       :root/authenticator      {}
                       :root/init-form          {}
                       :ui/router               {}
                       :root/global-error       {}
                       ::m.settings/site-config {}}}
  (let [{:keys [container pushable pusher top]}    (css/get-classnames Root)
        top-router-state                           (or (uism/get-active-state this ::RootRouter) :initial)
        {::m.settings/keys [loaded? initialized?]} site-config
        root                                       (uism/get-active-state this ::auth/auth-machine)
        gathering-credentials?                     (#{:state/gathering-credentials} root)]
    (dom/div {:classes [:.ui :.container container]}
      (if loaded?
        (if initialized?
          (comp/fragment
           (u.navbar/ui-navbar navbar)
           (dom/div {:classes [:.ui :.container :.fluid top]}
             (ui-sidebar-pushable
              {:className (string/join " " [pushable])}
              (u.navbar/ui-navbar-sidebar navbar)
              (ui-sidebar-pusher
               {:className (string/join " " [pusher])}
               (if (= :initial top-router-state)
                 (dom/div :.loading "Loading...")
                 (comp/fragment
                  (ui-global-error-display global-error)
                  (u.authenticator/ui-authenticator authenticator)
                  (when-not gathering-credentials?
                    (ui-root-router router))))))))
          (u.initialize/ui-init-form init-form))
        (dom/div {}
          (dom/p "Not loaded")))
      (inj/style-element {:component Root}))))
