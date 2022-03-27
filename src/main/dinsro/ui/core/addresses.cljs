(ns dinsro.ui.core.addresses
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.core.addresses :as m.core-addresses]
   [dinsro.mutations.core.addresses :as mu.core-addresses]))

(form/defsc-form CoreAddressForm
  [_this _props]
  {fo/id           m.core-addresses/id
   fo/title        "Address"
   fo/attributes   [m.core-addresses/address]
   fo/route-prefix "core-address"})

(def new-button
  {:type   :button
   :local? true
   :label  "New"
   :action (fn [this _] (form/create! this CoreAddressForm))})

(defn fetch-action
  [report-instance {::m.core-addresses/keys [id]}]
  (comp/transact! report-instance [(mu.core-addresses/fetch! {::m.core-addresses/id id})]))

(def fetch-button
  {:label     "Fetch"
   :action    fetch-action
   :disabled? (fn [_ row-props] (:account/active? row-props))})

(report/defsc-report CoreAddressReport
  [_this _props]
  {ro/columns          [m.core-addresses/address]
   ro/control-layout   {:action-buttons [::new]}
   ro/controls         {::new new-button}
   ro/source-attribute ::m.core-addresses/index
   ro/title            "Core Addresses"
   ro/row-actions      [fetch-button]
   ro/row-pk           m.core-addresses/id
   ro/run-on-mount?    true
   ro/route            "core-addresses"})
