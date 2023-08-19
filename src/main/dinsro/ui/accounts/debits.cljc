(ns dinsro.ui.accounts.debits
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.debits :as j.debits]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.debits :as m.debits]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../joins/debits.cljc]]
;; [[../../model/debits.cljc]]

(def index-page-key :accounts-show-debits)
(def model-key ::m.debits/id)
(def parent-model-key ::m.accounts/id)
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
   ro/row-pk           m.debits/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.debits/index
   ro/title            "Debits"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]
          :as      props}]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [[::dr/id router-key]
                       ::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["debits"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)}
  (log/debug :SubPage/starting {:props props})
  (if report
    (ui-report report)
    (ui-segment {:color "red" :inverted true}
      "Failed to load page")))
