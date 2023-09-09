(ns dinsro.ui.admin.core.addresses
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.core.addresses :as j.c.addresses]
   [dinsro.model.core.addresses :as m.c.addresses]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.mutations.core.addresses :as mu.c.addresses]
   [dinsro.options.core.addresses :as o.c.addresses]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.forms.admin.core.addresses :as u.f.a.c.addresses]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../joins/core/addresses.cljc]]
;; [[../../../model/core/addresses.cljc]]
;; [[../../../ui/core/addresses.cljs]]

(def index-page-id :admin-core-addresses)
(def model-key o.c.addresses/id)
(def parent-router-id :admin-core)
(def required-role :admin)
(def show-page-id :admin-core-addresses-show)

(def fetch-action
  (u.buttons/row-action-button "Fetch" model-key mu.c.addresses/delete!))

(def new-button
  {:type   :button
   :local? true
   :label  "New"
   :action (fn [this _] (form/create! this u.f.a.c.addresses/NewForm))})

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {o.c.addresses/address #(u.links/ui-admin-address-link %3)}
   ro/columns           [m.c.addresses/address]
   ro/control-layout    {:action-buttons [::new]}
   ro/controls          {::new new-button}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [fetch-action]
   ro/row-pk            m.c.addresses/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.addresses/admin-index
   ro/title             "Core Addresses"})

(def ui-report (comp/factory Report))

(defsc Show
  [_this {::m.c.addresses/keys [id address]
          :as                  props}]
  {:ident         (fn [] [o.navlinks/id show-page-id])
   :initial-state (fn [props]
                    {model-key             (model-key props)
                     o.c.addresses/address ""})
   :pre-merge     (u.loader/page-merger model-key {})
   :query         (fn []
                    [o.c.addresses/id
                     o.c.addresses/address])}
  (log/info :Show/starting {:props props})
  (if id
    (dom/div {}
      (ui-segment {}
        (dom/dl {}
          (dom/dt {} "Id")
          (dom/dd {} (str id))
          (dom/dt {} "Address")
          (dom/dd {} (str address)))))
    (u.debug/load-error props "admin address record")))

(def ui-show (comp/factory Show))

(defsc IndexPage
  [_this {:ui/keys [report]}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [o.navlinks/id index-page-id])
   :initial-state     (fn [_props]
                        {o.navlinks/id index-page-id
                         :ui/report    (comp/get-initial-state Report {})})
   :query             (fn []
                        [o.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["addresses"]
   :will-enter        (u.loader/page-loader index-page-id)}
  (dom/div {}
    (ui-report report)))

(defsc ShowPage
  [_this props]
  {:ident         (fn [] [o.navlinks/id show-page-id])
   :initial-state (fn [props]
                    {model-key         (model-key props)
                     o.navlinks/id     show-page-id
                     o.navlinks/target (comp/get-initial-state Show {})})
   :query         (fn []
                    [o.navlinks/id
                     o.c.addresses/id
                     {o.navlinks/target (comp/get-query Show)}])
   :route-segment ["address" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-id model-key ::ShowPage)}
  (u.loader/show-page props model-key ui-show))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::IndexPage
   o.navlinks/description   "Admin index for core addresses"
   o.navlinks/label         "Addresses"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})

(m.navlinks/defroute show-page-id
  {o.navlinks/control       ::ShowPage
   o.navlinks/description   "Admin core address"
   o.navlinks/label         "Show Address"
   o.navlinks/input-key     model-key
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    index-page-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
