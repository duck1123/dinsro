(ns dinsro.ui.home.accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.collections.table.ui-table :refer [ui-table]]
   [com.fulcrologic.semantic-ui.collections.table.ui-table-body :refer [ui-table-body]]
   [com.fulcrologic.semantic-ui.collections.table.ui-table-cell :refer [ui-table-cell]]
   [com.fulcrologic.semantic-ui.collections.table.ui-table-header :refer [ui-table-header]]
   [com.fulcrologic.semantic-ui.collections.table.ui-table-header-cell :refer [ui-table-header-cell]]
   [com.fulcrologic.semantic-ui.collections.table.ui-table-row :refer [ui-table-row]]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.accounts :as j.accounts]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.ui.forms.accounts :as u.f.accounts]
   [dinsro.ui.links :as u.links]))

;; [[../../joins/accounts.cljc]]
;; [[../../model/accounts.cljc]]

(def model-key ::m.accounts/id)

(def override-account-report false)
(def show-account-controls false)

(def new-account-button
  {:type   :button
   :local? true
   :label  "New"
   :action (fn [this _] (form/create! this u.f.accounts/NewForm))})

(defsc BodyItem
  [_this {::m.accounts/keys [currency]
          :as               props}]
  {:ident         ::m.accounts/id
   :initial-state {::m.accounts/id            nil
                   ::m.accounts/name          ""
                   ::m.accounts/currency      {}}
   :query         [::m.accounts/id
                   ::m.accounts/name
                   {::m.accounts/currency (comp/get-query u.links/CurrencyLinkForm)}]}
  (ui-table-row {}
    (ui-table-cell {} (u.links/ui-account-link props))
    (ui-table-cell {} (u.links/ui-currency-link currency))))

(def ui-body-item (comp/factory BodyItem {:keyfn ::m.accounts/id}))

(report/defsc-report Report
  [this props]
  {ro/BodyItem          BodyItem
   ro/columns           [m.accounts/id]
   ro/control-layout    {:action-buttons [::new ::refresh]}
   ro/controls          {::new     new-account-button
                         ::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/route             "accounts"
   ro/row-pk            m.accounts/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.accounts/index
   ro/title             "Accounts"}
  (let [{:ui/keys [current-rows]} props]
    (if override-account-report
      (report/render-layout this)
      (ui-segment {}
        (dom/h1 {} "Accounts")
        (when show-account-controls ((report/control-renderer this) this))
        (ui-table {}
          (ui-table-header {}
            (ui-table-row {}
              (ui-table-header-cell {} "Name")
              (ui-table-header-cell {} "Currency")))
          (ui-table-body {}
            (map ui-body-item current-rows)))))))

(def ui-report (comp/factory Report))
