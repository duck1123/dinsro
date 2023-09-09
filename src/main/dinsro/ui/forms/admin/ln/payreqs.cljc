(ns dinsro.ui.forms.admin.ln.payreqs
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [dinsro.model.ln.payreqs :as m.ln.payreqs]
   [dinsro.mutations.ln.payreqs :as mu.ln.payreqs]
   [lambdaisland.glogc :as log]))

(def decode-button
  {:type   :button
   :local? true
   :label  "Decode"
   :action (fn [this _]
             (let [props (comp/props this)]
               (log/info :decode-button/clicked {:props props})
               (comp/transact! this [`(mu.ln.payreqs/decode ~props)])))})

(form/defsc-form NewForm
  [_this _props]
  {fo/action-buttons [::decode]
   fo/attributes     [m.ln.payreqs/payment-request]
   fo/controls       {::decode decode-button}
   fo/id             m.ln.payreqs/id
   fo/route-prefix   "new-request"
   fo/title          "New Payreqs"})
