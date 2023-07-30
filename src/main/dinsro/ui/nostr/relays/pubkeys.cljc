(ns dinsro.ui.nostr.relays.pubkeys
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
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
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../joins/nostr/pubkeys.cljc]]
;; [[../../model/nostr/pubkeys.cljc]]
;; [[../../model/nostr/relays.cljc]]
;; [[../../model/nostr/relay_pubkeys.cljc]]
;; [[../../mutations/nostr/pubkeys.cljc]]
;; [[../../ui/nostr/relays.cljs]]

(def index-page-key :nostr-relays-show-pubkeys)
(def model-key ::m.n.pubkeys/id)
(def parent-model-key ::m.n.relays/id)
(def router-key :dinsro.ui.nostr.relays/Router)

(def fetch-action
  (u.buttons/subrow-action-button "Fetch" model-key parent-model-key mu.n.pubkeys/fetch!))

(def fetch-contacts-action
  (u.buttons/subrow-action-button "Fetch Contacts" model-key parent-model-key mu.n.pubkeys/fetch-contacts!))

(def fetch-events-action
  (u.buttons/subrow-action-button "Fetch Events" model-key parent-model-key mu.n.events/fetch-events!))

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
   ro/row-actions       [fetch-action
                         fetch-events-action
                         fetch-contacts-action]
   ro/row-pk            m.n.pubkeys/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.pubkeys/index
   ro/title             "Pubkeys"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]
          :as      props}]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     (fn [props]
                        {parent-model-key (parent-model-key props)
                         ::m.navlinks/id  index-page-key
                         :ui/report       (comp/get-initial-state Report {})})
   :query             (fn []
                        [[::dr/id router-key]
                         parent-model-key
                         ::m.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["pubkeys"]}
  (log/info :SubPage/starting {:props props})
  (ui-report report))

(m.navlinks/defroute index-page-key
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/label         "Pubkeys"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    :nostr-relays-show
   ::m.navlinks/router        :nostr-relays
   ::m.navlinks/required-role :user})
