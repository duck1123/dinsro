(ns dinsro.ui.user-pubkeys
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.nostr.pubkeys :as j.n.pubkeys]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.users :as m.users]
   [dinsro.ui.links :as u.links]))

;; [[../actions/user_pubkeys.clj][User Pubkeys Actions]]
;; [[../joins/users.cljc][User Joins]]
;; [[../queries/user_pubkeys.clj][User Pubkeys Queries]]
;; [[../model/user_pubkeys.cljc][User Pubkeys Model]]

(def ident-key ::m.users/id)
(def router-key :dinsro.ui.users/Router)

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.n.pubkeys/hex]
   ro/control-layout   {:inputs         [[::m.users/id]]
                        :action-buttons [::refresh]}
   ro/controls         {::m.users/id {:type :uuid :label "id"}
                        ::refresh    u.links/refresh-control}
   ro/field-formatters {::m.n.pubkeys/hex #(u.links/ui-pubkey-link %3)}
   ro/row-pk           m.n.pubkeys/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.n.pubkeys/index
   ro/title            "Pubkeys"})

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :query             [[::dr/id router-key]
                       {:ui/report (comp/get-query Report)}]
   :ident             (fn [] [:component/id ::SubPage])
   :initial-state     {:ui/report {}}
   :route-segment     ["pubkeys"]}
  ((comp/factory Report) report))