(ns dinsro.ui.accounts.debits
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.debits :as j.debits]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.debits :as m.debits]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

;; [[../../joins/debits.cljc]]
;; [[../../model/debits.cljc]]

(def ident-key ::m.accounts/id)
(def model-key ::m.debits/id)
(def router-key :dinsro.ui.accounts/Router)

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.debits/value]
   ro/control-layout   {:inputs         [[::m.accounts/id]]
                        :action-buttons [::refresh]}
   ro/controls         {::m.accounts/id {:type :uuid :label "id"}
                        ::refresh       u.links/refresh-control}
   ro/machine          spr/machine
   ro/page-size        10
   ro/paginate?        true
   ro/row-pk           model-key
   ro/run-on-mount?    true
   ro/source-attribute ::j.debits/index
   ro/title            "Debits"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount (partial u.loader/subpage-loader ident-key router-key Report)
   :ident             (fn [] [:component/id ::SubPage])
   :initial-state     {:ui/report {}}
   :query             [[::dr/id router-key]
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["debits"]}
  (ui-report report))
