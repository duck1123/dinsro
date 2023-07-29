(ns dinsro.ui.admin.users
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.users :as j.users]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.users :as m.users]
   [dinsro.mutations.users :as mu.users]
   [dinsro.ui.admin.users.accounts :as u.a.u.accounts]
   [dinsro.ui.admin.users.categories :as u.a.u.categories]
   [dinsro.ui.admin.users.debits :as u.a.u.debits]
   [dinsro.ui.admin.users.ln-nodes :as u.a.u.ln-nodes]
   [dinsro.ui.admin.users.pubkeys :as u.a.u.pubkeys]
   [dinsro.ui.admin.users.transactions :as u.a.u.transactions]
   [dinsro.ui.admin.users.user-pubkeys :as u.a.u.user-pubkeys]
   [dinsro.ui.admin.users.wallets :as u.a.u.wallets]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.menus :as u.menus]
   [lambdaisland.glogc :as log]))

;; [[../../joins/users.cljc]]
;; [[../../model/users.cljc]]

(def index-page-key
  "The navlink id for indexing this model"
  :admin-users)
(def model-key
  "The model key for these pages"
  ::m.users/id)
(def show-menu-id
  :admin-users)
(def show-page-key
  "The navlink id of the show page"
  :admin-users-show)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.users/delete!))

(def debug-show false)
(def debug-show-page false)
(def debug-page false)

(defrouter Router
  [_this _props]
  {:router-targets
   [u.a.u.accounts/SubPage
    u.a.u.categories/SubPage
    u.a.u.debits/SubPage
    u.a.u.ln-nodes/SubPage
    u.a.u.pubkeys/SubPage
    u.a.u.transactions/SubPage
    u.a.u.wallets/SubPage
    u.a.u.user-pubkeys/SubPage]})

(def ui-router (comp/factory Router))

(m.navbars/defmenu show-menu-id
  {::m.navbars/parent :admin
   ::m.navbars/router ::Router
   ::m.navbars/children
   [u.a.u.accounts/index-page-key
    u.a.u.categories/index-page-key
    u.a.u.debits/index-page-key
    u.a.u.ln-nodes/index-page-key
    u.a.u.pubkeys/index-page-key
    u.a.u.transactions/index-page-key
    u.a.u.user-pubkeys/index-page-key
    u.a.u.wallets/index-page-key]})

(defsc Show
  [_this {::m.users/keys [id name role]
          :ui/keys       [nav-menu router]
          :as            props}]
  {:componentDidMount (fn [this]
                        (let [props (comp/props this)
                              id    (model-key props)]
                          (log/info :Show/starting
                            {:props props
                             :this  this
                             :id    id})
                          #_(when id
                              (let [ident [model-key id]]
                                (df/load! this ident  Show {:target ident})))))
   :ident             ::m.users/id
   :initial-state     (fn [props]
                        (log/info :Show/intial-state-starting {:props props})
                        (let [id (get props model-key)]
                          {::m.users/name ""
                           ::m.users/role nil
                           ::m.users/id   nil
                           :ui/nav-menu   (comp/get-initial-state u.menus/NavMenu
                                            {::m.navbars/id show-menu-id :id id})
                           :ui/router     (comp/get-initial-state Router)}))
   :pre-merge         (u.loader/page-merger model-key
                        {:ui/router   [Router {}]
                         :ui/nav-menu [u.menus/NavMenu {::m.navbars/id show-menu-id}]})
   :query             [::m.users/name
                       ::m.users/role
                       ::m.users/id
                       {:ui/nav-menu (comp/get-query u.menus/NavMenu)}
                       {:ui/router (comp/get-query Router)}]}
  (log/info :Show/starting {:props props})
  (if id
    (dom/div :.show
      (dom/div :.show-inner
        (ui-segment {}
          (dom/p {} "Show User " (str name))
          (dom/p {} "Id: " (str id))
          (dom/p {} "Name: " (str name))
          (dom/div {} (str role)))
        (if nav-menu
          (u.menus/ui-nav-menu nav-menu)
          (u.debug/load-error props "admin show user menu"))
        (if router
          (ui-router router)
          (u.debug/load-error props "admin show user router"))))
    (u.debug/load-error props "admin show user")))

(def ui-show (comp/factory Show))

(form/defsc-form UserForm
  [_this _props]
  {fo/attributes   [m.users/name
                    m.users/role
                    m.users/password]
   fo/cancel-route ["admin"]
   fo/id           m.users/id
   fo/route-prefix "admin-user"
   fo/title        "Admin User"})

(def new-button
  {:label  "New User"
   :type   :button
   :action (fn [this] (form/create! this UserForm))})

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.users/name              #(u.links/ui-admin-user-link %3)
                         ::j.users/account-count     #(u.links/ui-admin-user-accounts-count-link %3)
                         ::j.users/category-count    #(u.links/ui-admin-user-categories-count-link %3)
                         ::j.users/ln-node-count     #(u.links/ui-admin-user-ln-nodes-count-link %3)
                         ::j.users/transaction-count #(u.links/ui-admin-user-transactions-count-link %3)
                         ::j.users/wallet-count      #(u.links/ui-admin-user-wallets-count-link %3)}
   ro/columns           [m.users/name
                         m.users/role
                         j.users/account-count
                         j.users/category-count
                         j.users/ln-node-count
                         j.users/transaction-count
                         j.users/wallet-count]
   ro/controls          {::new-user new-button
                         ::refresh  u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [delete-action]
   ro/row-pk            m.users/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.users/admin-index
   ro/title             "Users"})

(def ui-report (comp/factory Report))

(defsc IndexPage
  [_this {:ui/keys [report] :as props}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :pre-merge         (u.loader/page-merger nil {:ui/report [Report {}]})
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["users"]
   :will-enter        (u.loader/page-loader index-page-key)}
  (log/info :Page/starting {:props props})
  (if report
    (ui-report report)
    (u.debug/load-error props "admin index users report")))

(defsc ShowPage
  [_this {::m.users/keys    [id]
          ::m.navlinks/keys [target]
          :as               props}]
  {:ident             (fn [] [::m.navlinks/id show-page-key])
   :initial-state     (fn [props]
                        (log/info :ShowPage/initial-state {:props props})
                        {::m.navlinks/id     show-page-key
                         ::m.users/id        nil
                         ::m.navlinks/target (comp/get-initial-state Show {})})
   :query             [::m.navlinks/id
                       ::m.users/id
                       {::m.navlinks/target (comp/get-query Show)}]
   :route-segment     ["user" :id]
   :will-enter        (u.loader/targeted-router-loader show-page-key model-key ::ShowPage)}
  (log/info :ShowPage/starting {:props props})
  (if id
    (dom/div :.show-page
      (if target
        (ui-show target)
        (u.debug/load-error props "Admin Show User Page target")))
    (u.debug/load-error props "Admin Show User Page")))

(m.navlinks/defroute index-page-key
  {::m.navlinks/control       ::IndexPage
   ::m.navlinks/label         "Users"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    :admin
   ::m.navlinks/router        :admin
   ::m.navlinks/required-role :admin})

(m.navlinks/defroute show-page-key
  {::m.navlinks/control       ::ShowPage
   ::m.navlinks/label         "Admin Show User"
   ::m.navlinks/input-key     model-key
   ::m.navlinks/model-key     model-key
   ::m.navlinks/navigate-key  u.a.u.accounts/index-page-key
   ::m.navlinks/parent-key    index-page-key
   ::m.navlinks/router        :admin
   ::m.navlinks/required-role :admin})
