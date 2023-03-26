(ns dinsro.ui.core.wallets.accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.accounts :as j.accounts]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.ui.links :as u.links]))

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.accounts/name
                        m.accounts/user]
   ro/control-layout   {:inputs         [[::m.c.wallets/id]]
                        :action-buttons [::refresh]}
   ro/controls         {::refresh        u.links/refresh-control
                        ::m.c.wallets/id {:type "uuid"}}
   ro/field-formatters {::m.accounts/name #(u.links/ui-account-link %3)
                        ::m.accounts/user #(u.links/ui-user-link %2)}
   ro/row-pk           m.accounts/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.accounts/index
   ro/title            "Accounts"})

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :ident             (fn [] [:component/id ::SubPage])
   :initial-state     {::m.c.wallets/id nil
                       :ui/report       {}}
   :query             [::m.c.wallets/id
                       {:ui/report (comp/get-query Report)}]}
  ((comp/factory Report) report))

(def ui-sub-page (comp/factory SubPage))