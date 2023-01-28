(ns dinsro.ui.user-pubkeys
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.users :as m.users]
   [dinsro.ui.links :as u.links]))

;; [[../actions/user_pubkeys.clj][User Pubkeys Actions]]
;; [[../queries/user_pubkeys.clj][User Pubkeys Queries]]
;; [[../model/user_pubkeys.cljc][User Pubkeys Model]]

(def ident-key ::m.users/id)
(def router-key :dinsro.ui.users/Router)

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.n.pubkeys/hex]
   ro/controls         {::m.users/id {:type :uuid :label "id"}
                        ::refresh    u.links/refresh-control}
   ro/control-layout   {:inputs         [[::m.users/id]]
                        :action-buttons [::refresh]}
   ro/field-formatters {::m.n.pubkeys/hex #(u.links/ui-pubkey-link %3)}
   ro/row-pk           m.n.pubkeys/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.n.pubkeys/index
   ro/title            "Pubkeys"})

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:query             [[::dr/id router-key]
                       {:ui/report (comp/get-query Report)}]
   :componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :route-segment     ["ln-nodes"]
   :initial-state     {:ui/report {}}
   :ident             (fn [] [:component/id ::SubPage])}
  ((comp/factory Report) report))
