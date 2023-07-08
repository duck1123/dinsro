(ns dinsro.ui.admin.core.addresses
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.core.addresses :as j.c.addresses]
   [dinsro.model.core.addresses :as m.c.addresses]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.mutations.core.addresses :as mu.c.addresses]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../joins/core/addresses.cljc]]
;; [[../../../model/core/addresses.cljc]]
;; [[../../../ui/core/addresses.cljs]]

(def index-page-key :admin-core-addresses)
(def model-key ::m.c.addresses/id)
(def show-page-key :admin-core-addresses-show)

(def fetch-action
  (u.buttons/row-action-button "Fetch" model-key mu.c.addresses/delete!))

(form/defsc-form NewForm
  [_this _props]
  {fo/attributes   [m.c.addresses/address]
   fo/id           m.c.addresses/id
   fo/route-prefix "address"
   fo/title        "Address"})

(def new-button
  {:type   :button
   :local? true
   :label  "New"
   :action (fn [this _] (form/create! this NewForm))})

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.c.addresses/address #(u.links/ui-admin-address-link %3)}
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
  {:ident         ::m.c.addresses/id
   :initial-state (fn [props]
                    (let [id (model-key props)]
                      {model-key               id
                       ::m.c.addresses/address ""}))
   :pre-merge     (u.loader/page-merger model-key {})
   :query         [::m.c.addresses/id
                   ::m.c.addresses/address]}
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
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["addresses"]
   :will-enter        (u.loader/page-loader index-page-key)}
  (dom/div {}
    (ui-report report)))

(defsc ShowPage
  [_this {::m.c.addresses/keys [id]
          ::m.navlinks/keys [target]
          :as               props}]
  {:ident         (fn [] [::m.navlinks/id show-page-key])
   :initial-state {::m.c.addresses/id nil
                   ::m.navlinks/id     show-page-key
                   ::m.navlinks/target {}}
   :query         [::m.navlinks/id
                   ::m.c.addresses/id
                   {::m.navlinks/target (comp/get-query Show)}]
   :route-segment ["address" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-key model-key ::ShowPage)}
  (log/info :ShowPage/starting {:props props})
  (if (and target id)
    (ui-show target)
    (u.debug/load-error props "admin show address")))
