(ns dinsro.ui.admin.users.user-pubkeys
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.user-pubkeys :as j.user-pubkeys]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.user-pubkeys :as m.user-pubkeys]
   [dinsro.model.users :as m.users]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../joins/nostr/pubkeys.cljc]]
;; [[../../../model/nostr/pubkeys.cljc]]

(def index-page-key :admin-users-show-user-pubkeys)
(def model-key ::m.user-pubkeys/id)
(def parent-model-key ::m.users/id)
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

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {::m.users/keys [id]
          :ui/keys       [report]
          :as            props}]
  {:componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       ::m.users/id    nil
                       :ui/report      {}}
   :query             [[::dr/id router-key]
                       ::m.navlinks/id
                       ::m.users/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["user-pubkeys"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)}
  (log/info :SubPage/starting {:props props})
  (if (and report id)
    (ui-report report)
    (u.debug/load-error props "admin user user pubkeys")))

(m.navlinks/defroute
  :admin-users-show-user-pubkeys
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/label         "User Pubkeys"
   ::m.navlinks/model-key     ::m.n.pubkeys/id
   ::m.navlinks/parent-key    :admin-users-show
   ::m.navlinks/router        :admin-users
   ::m.navlinks/required-role :admin})
