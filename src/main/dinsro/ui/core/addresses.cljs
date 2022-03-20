(ns dinsro.ui.core.addresses
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.core.addresses :as m.c.addresses]
   [dinsro.mutations.core.addresses :as mu.c.addresses]))

(form/defsc-form CoreAddressForm
  [_this _props]
  {fo/id           m.c.addresses/id
   fo/title        "Address"
   fo/attributes   [m.c.addresses/address]
   fo/route-prefix "address"})

(def new-button
  {:type   :button
   :local? true
   :label  "New"
   :action (fn [this _] (form/create! this CoreAddressForm))})

(defn fetch-action
  [report-instance {::m.c.addresses/keys [id]}]
  (comp/transact! report-instance [(mu.c.addresses/fetch! {::m.c.addresses/id id})]))

(def fetch-button
  {:label     "Fetch"
   :action    fetch-action
   :disabled? (fn [_ row-props] (:account/active? row-props))})

(report/defsc-report CoreAddressReport
  [_this _props]
  {ro/columns          [m.c.addresses/address]
   ro/control-layout   {:action-buttons [::new]}
   ro/controls         {::new new-button}
   ro/source-attribute ::m.c.addresses/index
   ro/title            "Core Addresses"
   ro/row-actions      [fetch-button]
   ro/row-pk           m.c.addresses/id
   ro/run-on-mount?    true
   ro/route            "addresses"})
