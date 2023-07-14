(ns dinsro.ui.admin.users.debits
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.debits :as j.debits]
   [dinsro.model.debits :as m.debits]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.users :as m.users]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../joins/debits.cljc]]
;; [[../../../model/debits.cljc]]

(def index-page-key :admin-users-show-debits)
(def model-key ::m.debits/id)
(def parent-model-key ::m.users/id)
(def router-key :dinsro.ui.admin.users/Router)

(report/defsc-report Report
  ;; "Debits belonging to a user"
  [_this _props]
  {ro/column-formatters {::m.debits/description #(u.links/ui-transaction-link %3)
                         ::m.debits/account     #(u.links/ui-account-link %2)
                         ::m.debits/transaction #(u.links/ui-transaction-link %2)
                         ::j.debits/currency    #(u.links/ui-currency-link %2)}
   ro/columns           [m.debits/account
                         m.debits/transaction
                         m.debits/value
                         j.debits/currency]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {parent-model-key {:type :uuid :label "id"}
                         ::refresh    u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.debits/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.debits/index
   ro/title             "Debits"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {::m.users/keys [id]
          :ui/keys       [report]
          :as            props}]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       ::m.users/id    nil
                       :ui/report      {}}
   :query             [[::dr/id router-key]
                       ::m.navlinks/id
                       ::m.users/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["debits"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)}
  (log/info :SubPage/starting {:props props})
  (if (and report id)
    (ui-report report)
    (u.debug/load-error props "admin user debits")))

(m.navlinks/defroute   :admin-users-show-debits
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/label         "Debits"
   ::m.navlinks/model-key     ::m.debits/id
   ::m.navlinks/parent-key    :admin-users-show
   ::m.navlinks/router        :admin-users
   ::m.navlinks/required-role :admin})
