(ns dinsro.ui.nostr.pubkeys.users
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.users :as j.users]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.users :as m.users]
   [dinsro.ui.core.transactions :as u.c.transactions]
   [dinsro.ui.links :as u.links]))

(def ident-key ::m.n.pubkeys/id)
(def router-key :dinsro.ui.nostr.pubkeys/Router)

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.users/name]
   ro/controls         {::m.n.pubkeys/id {:type :uuid :label "id"}
                        ::refresh      u.links/refresh-control}
   ro/control-layout   {:action-buttons [::refresh]}
   ro/source-attribute ::j.users/index-by-pubkey
   ro/title            "Users"
   ro/row-actions      [u.c.transactions/fetch-action-button u.c.transactions/delete-action-button]
   ro/row-pk           m.users/id
   ro/run-on-mount?    true})

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:query             [{:ui/report (comp/get-query Report)}
                       [::dr/id router-key]]
   :componentDidMount (partial u.links/subpage-loader ident-key router-key Report)
   :route-segment     ["users"]
   :initial-state     {:ui/report {}}
   :ident             (fn [] [:component/id ::SubPage])}
  ((comp/factory Report) report))
