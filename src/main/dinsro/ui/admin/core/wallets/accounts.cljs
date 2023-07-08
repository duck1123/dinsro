(ns dinsro.ui.admin.core.wallets.accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.accounts :as j.accounts]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogc :as log]))

;; [[../../../../joins/accounts.cljc]]
;; [[../../../../model/accounts.cljc]]

(def index-page-key :admin-core-wallets-show-accounts)
(def model-key ::m.accounts/id)
(def parent-model-key ::m.c.wallets/id)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.accounts/name #(u.links/ui-account-link %3)
                         ::m.accounts/user #(u.links/ui-user-link %2)}
   ro/columns           [m.accounts/name
                         m.accounts/user]
   ro/control-layout    {:inputs         [[parent-model-key]]
                         :action-buttons [::refresh]}
   ro/controls          {::refresh        u.links/refresh-control
                         parent-model-key {:type "uuid"}}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.accounts/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.accounts/admin-index
   ro/title             "Accounts"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {::m.c.wallets/keys [id]
          :ui/keys           [report]
          :as                props}]
  {:componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.c.wallets/id nil
                       ::m.navlinks/id  index-page-key
                       :ui/report       {}}
   :query             [::m.c.wallets/id
                       ::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]}
  (log/info :SubPage/starting {:props props})
  (if (and report id)
    (ui-report report)
    (u.debug/load-error props "admin show wallet accounts page")))

(def ui-sub-page (comp/factory SubPage))
