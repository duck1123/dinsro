(ns dinsro.ui.admin.users.user-pubkeys
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.user-pubkeys :as j.user-pubkeys]
   [dinsro.model.user-pubkeys :as m.user-pubkeys]
   [dinsro.model.users :as m.users]
   [dinsro.ui.links :as u.links]))

(def ident-key ::m.users/id)
(def router-key :dinsro.ui.admin.users/Router)

(report/defsc-report Report
  [_this _props]
  {ro/columns           [m.user-pubkeys/id]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {::m.users/id {:type :uuid :label "id"}
                         ::refresh    u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.user-pubkeys/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.user-pubkeys/index
   ro/title             "User-Pubkeys"})

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :ident             (fn [] [:component/id ::SubPage])
   :initial-state     {:ui/report {}}
   :query             [[::dr/id router-key]
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["user-pubkeys"]}
  ((comp/factory Report) report))
