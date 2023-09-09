(ns dinsro.ui.admin.core.wallets
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.core.wallets :as j.c.wallets]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.mutations.core.wallets :as mu.c.wallets]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.admin.core.wallets.accounts :as u.a.c.w.accounts]
   [dinsro.ui.admin.core.wallets.addresses :as u.a.c.w.addresses]
   [dinsro.ui.admin.core.wallets.words :as u.a.c.w.words]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.forms.admin.core.wallets :as u.f.a.c.wallets]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../joins/core/wallets.cljc]]
;; [[../../../model/core/wallets.cljc]]
;; [[../../../../test/dinsro/ui/core/wallets_test.cljs]]

(def index-page-id :admin-core-wallets)
(def model-key ::m.c.wallets/id)
(def parent-router-id :admin-core)
(def required-role :admin)
(def show-page-id :admin-core-wallets-show)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.c.wallets/delete!))

(def new-action-button
  {:type   :button
   :local? true
   :label  "New"
   :action (fn [this _] (form/create! this u.f.a.c.wallets/NewForm))})

(defsc Show
  "Show a wallet"
  [this {::m.c.wallets/keys [id name derivation key network user
                             ext-public-key ext-private-key]
         :ui/keys           [admin-addresses admin-words admin-accounts]
         :as                props}]
  {:ident         ::m.c.wallets/id
   :initial-state {::m.c.wallets/id              nil
                   ::m.c.wallets/name            ""
                   ::m.c.wallets/derivation      ""
                   ::m.c.wallets/key             ""
                   ::m.c.wallets/ext-private-key ""
                   ::m.c.wallets/ext-public-key  ""
                   ::m.c.wallets/network         {}
                   ::m.c.wallets/user            {}
                   :ui/admin-accounts            {}
                   :ui/admin-addresses           {}
                   :ui/admin-words               {}}
   :pre-merge     (u.loader/page-merger model-key
                    {:ui/admin-accounts  [u.a.c.w.accounts/SubPage {}]
                     :ui/admin-addresses [u.a.c.w.addresses/SubPage {}]
                     :ui/admin-words     [u.a.c.w.words/SubPage {}]})
   :query         [::m.c.wallets/id
                   ::m.c.wallets/name
                   ::m.c.wallets/derivation
                   {::m.c.wallets/network (comp/get-query u.links/NetworkLinkForm)}
                   {::m.c.wallets/user (comp/get-query u.links/UserLinkForm)}
                   ::m.c.wallets/key
                   ::m.c.wallets/ext-private-key
                   ::m.c.wallets/ext-public-key
                   {:ui/admin-accounts (comp/get-query u.a.c.w.accounts/SubPage)}
                   {:ui/admin-addresses (comp/get-query u.a.c.w.addresses/SubPage)}
                   {:ui/admin-words (comp/get-query u.a.c.w.words/SubPage)}
                   [df/marker-table '_]]}
  (log/info :Show/creating {:id id :props props :this this})
  (if id
    (dom/div {}
      (ui-segment {}
        (dom/h1 {} "Wallet")
        (dom/button {:onClick (fn [_] (comp/transact! this [`(mu.c.wallets/derive! {~model-key ~id})]))}
          "derive")
        (dom/dl {}
          (dom/dt {} "Name")
          (dom/dd {} (str name))
          (dom/dt {} "Derivation")
          (dom/dd {} (str derivation))
          (dom/dt {} "Key")
          (dom/dd {} (str key))
          (dom/dt {} "Network")
          (dom/dd {} (u.links/ui-network-link network))
          (dom/dt {} "User")
          (dom/dd {} (u.links/ui-user-link user))
          (dom/dt {} "Extended Public Key")
          (dom/dd {} ext-public-key)
          (dom/dt {} "Extended Private Key")
          (dom/dd {} ext-private-key)))
      (ui-segment {}
        (u.a.c.w.words/ui-sub-page admin-words))
      (ui-segment {}
        (u.a.c.w.accounts/ui-sub-page admin-accounts))
      (ui-segment {}
        (u.a.c.w.addresses/ui-sub-page admin-addresses)))
    (u.debug/load-error props "admin show wallet")))

(def ui-show (comp/factory Show))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.c.wallets/node #(u.links/ui-core-node-link %2)
                         ::m.c.wallets/name #(u.links/ui-admin-wallet-link %3)
                         ::m.c.wallets/user #(u.links/ui-user-link %2)}
   ro/columns           [m.c.wallets/name
                         m.c.wallets/user
                         m.c.wallets/derivation
                         m.c.wallets/ext-public-key
                         m.c.wallets/ext-private-key]
   ro/control-layout    {:action-buttons [::new]}
   ro/controls          {::new new-action-button}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [delete-action]
   ro/row-pk            m.c.wallets/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.wallets/admin-index
   ro/title             "Wallet Report"})

(def ui-report (comp/factory Report))

(defsc IndexPage
  [_this {:ui/keys [report]}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [o.navlinks/id index-page-id])
   :initial-state     (fn [_props]
                        {o.navlinks/id index-page-id
                         :ui/report      (comp/get-initial-state Report {})})
   :query             (fn []
                        [o.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["wallets"]
   :will-enter        (u.loader/page-loader index-page-id)}
  (dom/div {}
    (ui-report report)))

(defsc ShowPage
  [_this props]
  {:ident         (fn [] [o.navlinks/id show-page-id])
   :initial-state (fn [props]
                    {model-key (model-key props)
                     o.navlinks/id     show-page-id
                     o.navlinks/target (comp/get-initial-state Show {})})
   :query         (fn []
                    [model-key
                     o.navlinks/id
                     {o.navlinks/target (comp/get-query Show)}])
   :route-segment ["wallet" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-id model-key ::ShowPage)}
  (u.loader/show-page props model-key ui-show))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::IndexPage
   o.navlinks/description   "Admin index of wallets"
   o.navlinks/label         "Wallets"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})

(m.navlinks/defroute show-page-id
  {o.navlinks/control       ::ShowPage
   o.navlinks/description   "Admin Show Wallet"
   o.navlinks/label         "Show Wallet"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    index-page-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
