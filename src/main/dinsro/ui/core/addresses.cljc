(ns dinsro.ui.core.addresses
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.core.addresses :as j.c.addresses]
   [dinsro.model.core.addresses :as m.c.addresses]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.mutations.core.addresses :as mu.c.addresses]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.loader :as u.loader]))

;; [[../../joins/core/addresses.cljc]]
;; [[../../model/core/addresses.cljc]]

(def index-page-id :core-addresses)
(def model-key ::m.c.addresses/id)
(def parent-router-id :core)
(def required-role :user)

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
  {ro/columns          [m.c.addresses/address]
   ro/control-layout   {:action-buttons [::new]}
   ro/controls         {::new new-button}
   ro/machine          spr/machine
   ro/page-size        10
   ro/paginate?        true
   ro/row-actions      [fetch-action]
   ro/row-pk           m.c.addresses/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.c.addresses/index
   ro/title            "Core Addresses"})

(def ui-report (comp/factory Report))

(defsc IndexPage
  [_this {:ui/keys [report]}]
  {:ident         (fn [] [::m.navlinks/id index-page-id])
   :initial-state {::m.navlinks/id index-page-id
                   :ui/report      {}}
   :query         [::m.navlinks/id
                   {:ui/report (comp/get-query Report)}]
   :route-segment ["addresses"]
   :will-enter    (u.loader/page-loader index-page-id)}
  (dom/div {}
    (ui-report report)))
