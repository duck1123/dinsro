(ns dinsro.ui.admin.nostr.pubkeys.relays
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.relays :as j.n.relays]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../joins/nostr/relays.cljc]]
;; [[../../../model/nostr/relays.cljc]]

(def index-page-key :admin-nostr-pubkeys-show-relays)
(def model-key ::m.n.relays/id)
(def parent-model-key ::m.n.pubkeys/id)
(def router-key :dinsro.ui.nostr.pubkeys.relays/Router)

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.n.relays/id
                        m.n.relays/address]
   ro/control-layout   {:action-buttons [::refresh]}
   ro/controls         {parent-model-key {:type :uuid :label "id"}
                        ::refresh        u.links/refresh-control}
   ro/machine          spr/machine
   ro/page-size        10
   ro/paginate?        true
   ro/row-pk           m.n.relays/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.n.relays/admin-index
   ro/title            "Relays"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]
          :as      props}]
  {;; :componentDidMount (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     (fn [_]
                        {::m.navlinks/id  index-page-key
                         parent-model-key nil
                         :ui/report       {}})
   ;; :pre-merge     (u.loader/page-merger model-key
   ;;                  {:ui/report [Report {}]})
   :query             (fn [_]
                        [[::dr/id router-key]
                         ::m.navlinks/id
                         parent-model-key
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["connections"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)}
  (log/info :SubPage/starting {:props props})
  (if (get props parent-model-key)
    (if report
      (ui-report report)
      (u.debug/load-error props "admin pubkeys relays report"))
    (u.debug/load-error props "admin pubkeys relays page")))

(m.navlinks/defroute
  :admin-nostr-pubkeys-show-relays
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/label         "Relays"
   ::m.navlinks/model-key     ::m.n.relays/id
   ::m.navlinks/parent-key    :nostr-pubkeys-show
   ::m.navlinks/router        :nostr-pubkeys
   ::m.navlinks/required-role :user})
