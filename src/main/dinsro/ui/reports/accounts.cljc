(ns dinsro.ui.reports.accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid :refer [ui-grid]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid-column :refer [ui-grid-column]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid-row :refer [ui-grid-row]]
   [com.fulcrologic.semantic-ui.collections.table.ui-table :refer [ui-table]]
   [com.fulcrologic.semantic-ui.collections.table.ui-table-body :refer [ui-table-body]]
   [com.fulcrologic.semantic-ui.collections.table.ui-table-cell :refer [ui-table-cell]]
   [com.fulcrologic.semantic-ui.collections.table.ui-table-header :refer [ui-table-header]]
   [com.fulcrologic.semantic-ui.collections.table.ui-table-header-cell :refer [ui-table-header-cell]]
   [com.fulcrologic.semantic-ui.collections.table.ui-table-row :refer [ui-table-row]]
   [com.fulcrologic.semantic-ui.elements.button.ui-button-group :refer [ui-button-group]]
   [com.fulcrologic.semantic-ui.elements.list.ui-list-item :refer [ui-list-item]]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.accounts :as j.accounts]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.debits :as m.debits]
   [dinsro.mutations.accounts :as mu.accounts]
   [dinsro.options.accounts :as o.accounts]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.accounts :as u.f.accounts]
   [dinsro.ui.links :as u.links]))

(def model-key o.accounts/id)

(def override-report? false)
(def override-controls? false)
(def use-index-table? false)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.accounts/delete!))

(def new-button
  {:type   :button
   :local? true
   :label  "New"
   :action (fn [this _] (form/create! this u.f.accounts/NewForm))})

(defsc DebitLine
  [_this {::m.debits/keys [value]}]
  {:ident         ::m.debits/id
   :initial-state {::m.debits/id    nil
                   ::m.debits/value 0}
   :query         [::m.debits/id
                   ::m.debits/value]}
  (ui-list-item {}
    (str value)))

(def ui-debit-line (comp/factory DebitLine {:keyfn ::m.debits/id}))

(defsc BodyItem-list
  [this {::m.accounts/keys [currency initial-value wallet]
         ::j.accounts/keys [debit-count]
         :as               props}]
  {:ident         ::m.accounts/id
   :initial-state (fn [_props]
                    {o.accounts/id            nil
                     o.accounts/name          ""
                     o.accounts/currency      (comp/get-initial-state u.links/CurrencyLinkForm {})
                     o.accounts/initial-value 0
                     o.accounts/wallet        (comp/get-initial-state u.links/WalletLinkForm {})
                     ::j.accounts/debit-count 0
                     ::j.accounts/debits      []})
   :query         (fn []
                    [o.accounts/id
                     o.accounts/name
                     {o.accounts/currency (comp/get-query u.links/CurrencyLinkForm)}
                     o.accounts/initial-value
                     {o.accounts/wallet (comp/get-query u.links/WalletLinkForm)}
                     ::j.accounts/debit-count
                     {::j.accounts/debits (comp/get-query DebitLine)}])}
  (ui-grid-column {:computer 4 :tablet 8 :mobile 16}
    (ui-segment {}
      (dom/div {}
        (dom/div {}
          (u.links/ui-account-link props)
          " (" (u.links/ui-currency-link currency) ")")
        (dom/div {}
          (str "Initial Value: " initial-value))
        (dom/div {}
          (when wallet
            (dom/span {}
              (dom/span {} "Wallet: ")
              (u.links/ui-wallet-link wallet))))
        (dom/div {} "Debit count: " (str debit-count)))
      (dom/div {}
        (dom/div {} (u.buttons/delete-button `mu.accounts/delete! model-key this))))))

(def ui-body-item-list (comp/factory BodyItem-list {:keyfn o.accounts/id}))

(defsc BodyItem-table
  [this {::m.accounts/keys [currency initial-value wallet]
         ::j.accounts/keys [debit-count]
         :as               props}]
  {:ident         ::m.accounts/id
   :initial-state (fn [_props]
                    {o.accounts/id            nil
                     o.accounts/name          ""
                     o.accounts/currency      (comp/get-initial-state u.links/CurrencyLinkForm {})
                     o.accounts/initial-value 0
                     o.accounts/wallet        (comp/get-initial-state u.links/WalletLinkForm {})
                     ::j.accounts/debit-count 0
                     ::j.accounts/debits      []})
   :query         (fn []
                    [o.accounts/id
                     o.accounts/name
                     {o.accounts/currency (comp/get-query u.links/CurrencyLinkForm)}
                     o.accounts/initial-value
                     {o.accounts/wallet (comp/get-query u.links/WalletLinkForm)}
                     ::j.accounts/debit-count
                     {::j.accounts/debits (comp/get-query DebitLine)}])}
  (ui-table-row {}
    (ui-table-cell {} (u.links/ui-account-link props))
    (ui-table-cell {} (u.links/ui-currency-link currency))
    (ui-table-cell {} (str initial-value))
    (ui-table-cell {} (when wallet (u.links/ui-wallet-link wallet)))
    (ui-table-cell {} (str debit-count))
    (ui-table-cell {} (u.buttons/delete-button `mu.accounts/delete! model-key this))))

(def BodyItem BodyItem-table)

(def ui-body-item-table (comp/factory BodyItem-table {:keyfn ::m.accounts/id}))
(def ui-body-item ui-body-item-table)

(report/defsc-report Report
  [this props]
  {ro/BodyItem          BodyItem
   ro/column-formatters {::m.accounts/currency #(u.links/ui-currency-link %2)
                         ::m.accounts/user     #(u.links/ui-user-link %2)
                         ::m.accounts/name     #(u.links/ui-account-link %3)
                         ::m.accounts/wallet   #(when %2 (u.links/ui-wallet-link %2))
                         ::m.accounts/source   #(u.links/ui-rate-source-link %2)}
   ro/columns           [m.accounts/name
                         m.accounts/currency
                         m.accounts/initial-value
                         m.accounts/wallet
                         j.accounts/debit-count]
   ro/control-layout    {:action-buttons [::new ::refresh]}
   ro/controls          {::new     new-button
                         ::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [delete-action]
   ro/row-pk            m.accounts/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.accounts/index
   ro/title             "Accounts"}
  (let [{:ui/keys [current-rows]} props]
    (if override-report?
      (report/render-layout this)
      (dom/div {}
        (ui-segment {}
          (dom/h1 {}
            (ui-grid {}
              (ui-grid-row {}
                (ui-grid-column {:width 8}
                  (dom/span {} "Accounts"))
                (ui-grid-column {:floated "right" :width 8}
                  (when-not override-controls?
                    (ui-button-group {:compact true :floated "right"}
                      (u.buttons/create-button this u.f.accounts/NewForm)
                      (u.buttons/refresh-button this))))))))

        (if override-controls?
          ((report/control-renderer this) this)
          (comment (dom/div {}
                     "controls")))

        (if use-index-table?
          (ui-table {}
            (ui-table-header {}
              (ui-table-row {}
                (ui-table-header-cell {} "Name")
                (ui-table-header-cell {} "Currency")
                (ui-table-header-cell {} "Initial Value")
                (ui-table-header-cell {} "Wallet")
                (ui-table-header-cell {} "Debit Count")))
            (ui-table-body {}
              (map ui-body-item current-rows)))
          (ui-segment {}
            (ui-grid {}
              (ui-grid-row {}
                (map ui-body-item-list current-rows)))))))))

(def ui-report (comp/factory Report))
