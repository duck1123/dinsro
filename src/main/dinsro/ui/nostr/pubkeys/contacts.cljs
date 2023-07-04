(ns dinsro.ui.nostr.pubkeys.contacts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.pubkeys :as j.n.pubkeys]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

;; [[../../joins/nostr/pubkey_contacts.cljc][Pubkey Contacts Joins]]
;; [[../../model/nostr/pubkey_contacts.cljc][Pubkey Contacts Model]]

(def index-page-key :nostr-pubkeys-contacts)
(def parent-model-key ::m.n.pubkeys/id)
(def router-key :dinsro.ui.nostr.pubkeys/Router)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.pubkeys/hex  #(u.links/ui-pubkey-link %3)
                         ::m.n.pubkeys/name #(u.links/ui-pubkey-name-link %3)
                         ::m.n.pubkeys/picture
                         (fn [_ picture]
                           (when picture
                             (dom/img {:src    (str picture)
                                       :width  100
                                       :height 100})))}
   ro/columns           [m.n.pubkeys/picture
                         m.n.pubkeys/name
                         j.n.pubkeys/contact-count
                         j.n.pubkeys/event-count]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {::m.n.pubkeys/id {:type :uuid :label "id"}
                         ::refresh        u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.n.pubkeys/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.pubkeys/contacts
   ro/title             "Contacts"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [[::dr/id router-key]
                       ::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["contacts"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)}
  (ui-report report))
