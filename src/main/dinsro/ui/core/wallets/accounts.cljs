(ns dinsro.ui.core.wallets.accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   ;; [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.accounts :as j.accounts]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogc :as log]))

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.accounts/name
                        m.accounts/user]
   ro/controls         {::refresh u.links/refresh-control
                        ::m.c.wallets/id {:type "uuid"}}
   ro/control-layout  {:inputs [[::m.c.wallets/id]]
                       :action-buttons [::refresh]}
   ro/field-formatters {::m.accounts/name #(u.links/ui-account-link %3)
                        ::m.accounts/user #(u.links/ui-user-link %2)}
   ro/row-pk           m.accounts/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.accounts/index
   ro/title            "Accounts"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report] :as props}]
  {:query             [::m.c.wallets/id
                       {:ui/report (comp/get-query Report)}]
   :componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :initial-state     {::m.c.wallets/id nil
                       :ui/report       {}}
   :ident             (fn [] [:component/id ::SubPage])}
  (log/info :SubPage/creating {:props props})
  (when report
    (ui-report report)))

(def ui-sub-page (comp/factory SubPage))
