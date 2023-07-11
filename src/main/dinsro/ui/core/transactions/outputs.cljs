(ns dinsro.ui.core.transactions.outputs
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.core.tx-out :as j.c.tx-out]
   [dinsro.model.core.transactions :as m.c.transactions]
   [dinsro.model.core.tx-out :as m.c.tx-out]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   ;; [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../joins/core/tx_in.cljc]]
;; [[../../../model/core/tx_in.cljc]]

(def index-page-key :core-transactions-show-outputs)
(def model-key ::m.c.tx-out/id)
(def parent-model-key ::m.c.transactions/id)

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.c.tx-out/n
                        m.c.tx-out/value
                        m.c.tx-out/hex
                        m.c.tx-out/type]
   ro/control-layout   {:action-buttons [::refresh]}
   ro/controls         {::refresh             u.links/refresh-control
                        parent-model-key {:type :uuid :label "TX"}}
   ro/machine          spr/machine
   ro/page-size        10
   ro/paginate?        true
   ro/row-pk           m.c.tx-out/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.c.tx-out/index
   ro/title            "Outputs"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]
          :as      props}]
  {:componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     (fn [props]
                        (let [id (get props parent-model-key)]
                          {parent-model-key id
                           ::m.navlinks/id index-page-key
                           :ui/report      {}}))
   :query             (fn [_props]
                        [::m.navlinks/id
                         parent-model-key
                         {:ui/report (comp/get-query Report)}])
   ;; :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)
   }
  (log/info :SubPage/starting {:props props})
  (if (get props parent-model-key)
    (ui-report report)
    (u.debug/load-error props "core transactions outputs")))
