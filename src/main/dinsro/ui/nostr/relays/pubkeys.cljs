(ns dinsro.ui.nostr.relays.pubkeys
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.pubkeys :as j.n.pubkeys]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.mutations.nostr.events :as mu.n.events]
   [dinsro.mutations.nostr.pubkeys :as mu.n.pubkeys]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

;; [[../../joins/nostr/pubkeys.cljc]]
;; [[../../model/nostr/pubkeys.cljc]]
;; [[../../model/nostr/relays.cljc]]
;; [[../../model/nostr/relay_pubkeys.cljc]]
;; [[../../mutations/nostr/pubkeys.cljc]]
;; [[../../ui/nostr/relays.cljs]]

(def ident-key ::m.n.relays/id)
(def index-page-key :nostr-relays-show-pubkeys)
(def router-key :dinsro.ui.nostr.relays/Router)

(form/defsc-form AddForm
  [_this _props]
  {fo/attributes   [m.n.pubkeys/hex]
   fo/id           m.n.pubkeys/id
   fo/route-prefix "new-pubkey"
   fo/title        "Pubkey"})

(def new-button
  {:type   :button
   :local? true
   :label  "New"
   :action (fn [this _] (form/create! this AddForm))})

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.pubkeys/name #(u.links/ui-pubkey-name-link %3)
                         ::m.n.pubkeys/picture
                         (fn [_ picture] (if picture
                                           (dom/img {:src picture :width 100 :height 100})
                                           ""))}
   ro/columns           [m.n.pubkeys/picture
                         m.n.pubkeys/name]
   ro/control-layout    {:action-buttons [::new ::refresh]
                         :controls       [::m.n.relays/id]}
   ro/controls          {::m.n.relays/id {:type :uuid :label "id"}
                         ::new           new-button
                         ::refresh       u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [(u.buttons/subrow-action-button "Fetch" ::m.n.pubkeys/id ident-key  mu.n.pubkeys/fetch!)
                         (u.buttons/subrow-action-button "Fetch Events" ::m.n.pubkeys/id ident-key  mu.n.events/fetch-events!)
                         (u.buttons/subrow-action-button "Fetch Contacts" ::m.n.pubkeys/id ident-key  mu.n.pubkeys/fetch-contacts!)]
   ro/row-pk            m.n.pubkeys/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.pubkeys/index
   ro/title             "Pubkeys"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount (partial u.loader/subpage-loader ident-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [[::dr/id router-key]
                       ::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["pubkeys"]}
  (ui-report report))
