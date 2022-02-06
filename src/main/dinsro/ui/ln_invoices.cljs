(ns dinsro.ui.ln-invoices
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.rendering.semantic-ui.field :refer [render-field-factory]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.ln-invoices :as m.ln-invoices]
   [dinsro.mutations.ln-invoices :as mu.ln-invoices]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.scanner :as u.scanner]
   [taoensso.timbre :as log]))

(defsc Scanner
  [_this _props]
  (dom/div {}
    (u.scanner/ui-scanner {} {:onScan (fn [data] (log/infof "scanned: %s" data))})))

(defsc LnInvoiceRow
  [_this {::m.ln-invoices/keys [amount-paid
                                add-index
                                memo
                                value
                                state
                                settled?] :as props}]
  {:ident ::m.ln-invoices/id
   :query [::m.ln-invoices/id
           ::m.ln-invoices/memo
           ::m.ln-invoices/amount-paid
           ::m.ln-invoices/add-index
           ::m.ln-invoices/value
           ::m.ln-invoices/state
           ::m.ln-invoices/settled?]}
  (dom/tr {}
    (dom/td (str add-index))
    (dom/td (u.links/ui-invoice-link props))
    (dom/td (str memo))
    (dom/td (str value))
    (dom/td (str settled?))
    (dom/td (str state))
    (dom/td (str amount-paid))))

(def ui-ln-invoice-row (comp/factory LnInvoiceRow {:keyfn ::m.ln-invoices/id}))

(defn ref-ln-invoice-row
  [{:keys [value]} _attribute]
  (comp/fragment
   (dom/table :.ui.table
     (dom/thead {}
       (dom/tr {}
         (dom/th {} "Index")
         (dom/th {} "Preimage")
         (dom/th {} "Memo")
         (dom/th {} "Value")
         (dom/th {} "Settled?")
         (dom/th {} "State")
         (dom/th {} "Amount Paid")))
     (dom/tbody {}
       (for [tx value]
         (ui-ln-invoice-row tx))))))

(def render-ref-ln-invoice-row (render-field-factory ref-ln-invoice-row))

(form/defsc-form InvoiceSubForm [_this _props]
  {fo/id         m.ln-invoices/id
   fo/attributes [m.ln-invoices/ammount-paid
                  m.ln-invoices/add-index
                  m.ln-invoices/value
                  m.ln-invoices/payment-request
                  m.ln-invoices/state
                  m.ln-invoices/settled?
                  m.ln-invoices/r-preimage
                  m.ln-invoices/fallback-address
                  m.ln-invoices/settle-date
                  m.ln-invoices/settle-index
                  m.ln-invoices/description-hash
                  m.ln-invoices/memo]
   fo/title      "Lightning Invoices"})

(form/defsc-form LNInvoiceForm [_this _props]
  {fo/id           m.ln-invoices/id
   fo/attributes   [m.ln-invoices/ammount-paid
                    m.ln-invoices/add-index
                    m.ln-invoices/cltv-expiry
                    m.ln-invoices/expiry
                    m.ln-invoices/private?
                    m.ln-invoices/keysend?
                    m.ln-invoices/value
                    m.ln-invoices/r-hash
                    m.ln-invoices/r-preimage
                    m.ln-invoices/payment-request
                    m.ln-invoices/state
                    m.ln-invoices/settled?
                    m.ln-invoices/fallback-address
                    m.ln-invoices/settle-date
                    m.ln-invoices/settle-index
                    m.ln-invoices/description-hash
                    m.ln-invoices/amp?
                    m.ln-invoices/memo
                    m.ln-invoices/node]
   fo/subforms     {::m.ln-invoices/node {fo/ui u.links/NodeLinkForm}}
   fo/route-prefix "ln-invoice"
   fo/title        "Lightning Invoices"})

(def submit-button
  {:type   :button
   :local? true
   :label  "Submit"
   :action (fn [this _key]
             (let [props (comp/props this)]
               (log/infof "submit: %s" props)
               (comp/transact! this [(mu.ln-invoices/submit! props)])))})

(form/defsc-form NewInvoiceForm [this props]
  {fo/id             m.ln-invoices/id
   fo/action-buttons [::submit]
   fo/attributes     [m.ln-invoices/memo
                      m.ln-invoices/value
                      m.ln-invoices/node]
   fo/controls       {::submit submit-button}
   fo/subforms       {::m.ln-invoices/node {fo/ui u.links/NodeLinkForm}}
   fo/route-prefix   "new-invoice"
   fo/title          "New Invoice"}
  (dom/div {}
    (form/render-layout this props)))

(def new-button
  {:type   :button
   :local? true
   :label  "New"
   :action (fn [this _] (form/create! this NewInvoiceForm))})

(report/defsc-report LNInvoicesReport
  [this _props]
  {ro/columns          [m.ln-invoices/id
                        m.ln-invoices/memo
                        m.ln-invoices/settled?
                        m.ln-invoices/creation-date
                        m.ln-invoices/node]
   ro/control-layout   {:action-buttons [::new]}
   ro/controls         {::new new-button}
   ro/links            {::m.ln-invoices/id (fn [this {::m.ln-invoices/keys [id]}]
                                             (form/view! this LNInvoiceForm id))}
   ro/field-formatters {::m.ln-invoices/node (fn [_this props] (u.links/ui-node-link props))}
   ro/route            "ln-invoices"
   ro/row-pk           m.ln-invoices/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.ln-invoices/index
   ro/title            "Lightning Invoices Report"}
  (dom/div {}
    (dom/h1 {} "Invoices")
    (report/render-layout this)))
