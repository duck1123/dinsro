(ns dinsro.ui.forms.admin.ln.invoices
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [dinsro.model.ln.invoices :as m.ln.invoices]
   [dinsro.mutations.ln.invoices :as mu.ln.invoices]
   [lambdaisland.glogc :as log]))

(def submit-button
  {:type   :button
   :local? true
   :label  "Submit"
   :action (fn [this _key]
             (let [props (comp/props this)]
               (log/info :submit-button/starting {:props props})
               (comp/transact! this [`(mu.ln.invoices/submit! ~props)])))})

(form/defsc-form NewForm [this props]
  {fo/action-buttons [::submit]
   fo/attributes     [m.ln.invoices/memo
                      m.ln.invoices/value]
   fo/controls       {::submit submit-button}
   fo/id             m.ln.invoices/id
   fo/route-prefix   "new-invoice"
   fo/title          "New Invoice"}
  (dom/div {}
    (form/render-layout this props)))
