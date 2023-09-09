(ns dinsro.ui.core.wallets.addresses
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.core.wallet-addresses :as j.c.wallet-addresses]
   [dinsro.model.core.wallet-addresses :as m.c.wallet-addresses]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.mutations.core.wallet-addresses :as mu.c.wallet-addresses]
   [dinsro.mutations.core.wallets :as mu.c.wallets]
   [dinsro.options.core.wallet-addresses :as o.c.wallet-addresses]
   [dinsro.options.core.wallets :as o.c.wallets]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.forms.core.wallets.addresses :as u.f.c.w.addresses]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogc :as log]))

;; [[../../../joins/core/addresses.cljc]]
;; [[../../../model/core/addresses.cljc]]

(def index-page-id :core-wallets-show-addresses)
(def model-key o.c.wallet-addresses/id)
(def parent-model-key o.c.wallets/id)

(def generate-action
  (u.buttons/row-action-button "Generate" model-key mu.c.wallet-addresses/generate!))

(def new-action-button
  {:type   :button
   :local? true
   :label  "New"
   :action (fn [this _] (form/create! this u.f.c.w.addresses/NewForm))})

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {o.c.wallet-addresses/address #(u.links/ui-admin-address-link %2)
                         o.c.wallet-addresses/wallet  #(u.links/ui-wallet-link %2)}
   ro/columns           [m.c.wallet-addresses/path-index
                         m.c.wallet-addresses/address]
   ro/control-layout    {:inputs         [[::m.c.wallets/id]]
                         :action-buttons [::new ::calculate ::refresh]}
   ro/controls          {o.c.wallets/id {:type :uuid :label "id"}
                         ::new            new-action-button
                         ::refresh        u.links/refresh-control
                         ::calculate      (u.buttons/report-action-button "Calculate" model-key mu.c.wallets/calculate-addresses!)}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/route             "wallets-addresses"
   ro/row-actions       [generate-action]
   ro/row-pk            m.c.wallet-addresses/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.wallet-addresses/index
   ro/title             "Addresses"})

(def ui-report (comp/factory Report))

(defsc SubSection
  [_this {::m.c.wallets/keys [id]
          :ui/keys           [report]
          :as                props}]
  {:componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :ident             (fn [] [o.navlinks/id index-page-id])
   :initial-state     (fn [props]
                        {parent-model-key (parent-model-key props)
                         o.navlinks/id    index-page-id
                         :ui/report       (comp/get-initial-state Report {})})
   :query             (fn []
                        [parent-model-key
                         o.navlinks/id
                         {:ui/report (comp/get-query Report)}])}
  (log/debug :SubSection/starting {:props props})
  (if (and report id)
    (ui-report report)
    (u.debug/load-error props "wallet addresses subsection")))

(def ui-sub-section (comp/factory SubSection))
