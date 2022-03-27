(ns dinsro.ui.core.addresses
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.core.addresses :as m.core-address]
   [dinsro.mutations.core.addresses :as mu.core-address]))

(form/defsc-form CoreAddressForm
  [_this _props]
  {fo/id           m.core-address/id
   fo/title        "Address"
   fo/attributes   [m.core-address/address]
   fo/route-prefix "core-address"})

(def new-button
  {:type   :button
   :local? true
   :label  "New"
   :action (fn [this _] (form/create! this CoreAddressForm))})

(defn fetch-action
  [report-instance {::m.core-address/keys [id]}]
  (comp/transact! report-instance [(mu.core-address/fetch! {::m.core-address/id id})]))

(def fetch-button
  {:label     "Fetch"
   :action    fetch-action
   :disabled? (fn [_ row-props] (:account/active? row-props))})

(report/defsc-report CoreAddressReport
  [_this _props]
  {ro/columns          [m.core-address/address]
   ro/control-layout   {:action-buttons [::new]}
   ro/controls         {::new new-button}
   ro/source-attribute ::m.core-address/index
   ro/title            "Core Addresses"
   ro/row-actions      [fetch-button]
   ro/row-pk           m.core-address/id
   ro/run-on-mount?    true
   ro/route            "core-addresses"})
