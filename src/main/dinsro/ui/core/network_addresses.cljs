(ns dinsro.ui.core.network-addresses
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.core.addresses :as m.c.addresses]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.mutations.core.addresses :as mu.c.addresses]
   [dinsro.ui.links :as u.links]))

(defn delete-action
  [report-instance {::m.c.addresses/keys [id]}]
  (form/delete! report-instance ::m.c.addresses/id id))

(defn fetch-action
  [report-instance {::m.c.addresses/keys [id]}]
  (comp/transact! report-instance [(mu.c.addresses/fetch! {::m.c.addresses/id id})]))

(def delete-action-button
  {:label  "Delete"
   :action delete-action
   :style  :delete-button})

(def fetch-action-button
  {:label  "Fetch"
   :action fetch-action
   :style  :fetch-button})

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.c.addresses/address]
   ro/controls         {::refresh      u.links/refresh-control
                        ::m.c.networks/id {:type :uuid :label "Nodes"}}
   ro/control-layout   {:inputs [[::m.c.networks/id]]
                        :action-buttons [::refresh]}
   ro/field-formatters {::m.c.addresses/height #(u.links/ui-block-height-link %3)}
   ro/source-attribute ::m.c.addresses/index-by-network
   ro/title            "Addresses"
   ro/row-actions      [fetch-action-button delete-action-button]
   ro/row-pk           m.c.addresses/id
   ro/run-on-mount?    true})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report] :as props}]
  {:query         [{:ui/report (comp/get-query Report)}
                   [::dr/id :dinsro.ui.core.networks/Router]]
   :initial-state {:ui/report {}}
   :route-segment ["addresses"]
   :ident         (fn [] [:component/id ::SubPage])}
  (let [router-info (get props [::dr/id :dinsro.ui.core.networks/Router])]
    (if (::m.c.networks/id router-info)
      (ui-report report)
      (dom/p {} "Network Addresses: Node ID not set"))))

(def ui-sub-page (comp/factory SubPage))