(ns dinsro.ui.nostr.relays
  (:require
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.nostr.relays :as j.n.relays]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.mutations.nostr.relays :as mu.n.relays]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.menus :as u.menus]
   [dinsro.ui.nostr.relays.connections :as u.n.r.connections]
   [dinsro.ui.nostr.relays.events :as u.n.r.events]
   [dinsro.ui.nostr.relays.pubkeys :as u.n.r.pubkeys]
   [dinsro.ui.nostr.relays.requests :as u.n.r.requests]
   [dinsro.ui.nostr.relays.runs :as u.n.r.runs]
   [dinsro.ui.nostr.relays.witnesses :as u.n.r.witnesses]
   [lambdaisland.glogc :as log]))

;; [[../../actions/nostr/relays.clj][Actions]]
;; [[../../model/nostr/relays.cljc][Model]]
;; [[../../mutations/nostr/relays.cljc][Mutations]]
;; [[../../queries/nostr/relays.clj][Queries]]

(def index-page-key :nostr-relays)
(def model-key ::m.n.relays/id)
(def show-page-key :nostr-relays-show)

(def submit-button
  {:type   :button
   :local? true
   :label  "Submit"
   :action (fn [this _]
             (let [props (comp/props this)
                   address (::m.n.relays/address props)]
               (log/info :submit-button/clicked {:address address})
               (comp/transact! this
                 [(mu.n.relays/submit! {::m.n.relays/address address})])))})

(form/defsc-form NewRelayForm [_this _props]
  {fo/action-buttons [::submit]
   fo/attributes     [m.n.relays/address]
   fo/cancel-route   ["relays"]
   fo/controls       {::submit submit-button}
   fo/id             m.n.relays/id
   fo/route-prefix   "new-relay"
   fo/title          "Relay"})

(def new-button
  {:type   :button
   :local? true
   :label  "New Relay"
   :action (fn [this _] (form/create! this NewRelayForm))})

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::j.n.relays/connection-count #(u.links/ui-relay-connection-count-link %3)
                         ::j.n.relays/request-count    #(u.links/ui-relay-request-count-link %3)
                         ::m.n.relays/address          #(u.links/ui-relay-link %3)}
   ro/columns           [m.n.relays/address
                         j.n.relays/request-count
                         j.n.relays/connection-count]
   ro/control-layout    {:action-buttons [::new ::refresh]}
   ro/controls          {::new     new-button
                         ::refresh u.links/refresh-control}
   ro/row-actions       [(u.buttons/row-action-button "Delete" ::m.n.relays/id mu.n.relays/delete!)]
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.n.relays/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.relays/index
   ro/title             "Relays Report"})

(def ui-report (comp/factory Report))

(defrouter Router
  [_this _props]
  {:router-targets
   [u.n.r.connections/SubPage
    u.n.r.pubkeys/SubPage
    u.n.r.requests/SubPage
    u.n.r.runs/SubPage
    u.n.r.events/SubPage
    u.n.r.witnesses/SubPage]})

(def ui-router (comp/factory Router))

(defsc Show
  [_this {::m.n.relays/keys [address id]
          ::j.n.relays/keys [connection-count]
          :ui/keys          [nav-menu router]}]
  {:ident         ::m.n.relays/id
   :initial-state (fn [{::m.n.relays/keys [id] :as props}]
                    (log/trace :Show/starting {:props props})
                    {::m.n.relays/id               nil
                     ::m.n.relays/address          ""
                     ::j.n.relays/connection-count 0
                     :ui/nav-menu                  (comp/get-initial-state u.menus/NavMenu {::m.navbars/id :nostr-relays :id id})
                     :ui/router                    (comp/get-initial-state Router)})
   :pre-merge     (u.loader/page-merger ::m.n.relays/id {:ui/router [Router {}]})
   :query         [::m.n.relays/id
                   ::m.n.relays/address
                   ::j.n.relays/connection-count
                   {:ui/nav-menu (comp/get-query u.menus/NavMenu)}
                   {:ui/router (comp/get-query Router)}]}
  (if id
    (let [{:keys [main _sub]} (css/get-classnames Show)]
      (dom/div {:classes [main]}
        (ui-segment {}
          (dom/dl {}
            (dom/dt {} "Address")
            (dom/dd {} (str address))
            (dom/dt {} "Connections")
            (dom/dd {} (str connection-count))))
        (u.menus/ui-nav-menu nav-menu)
        (ui-router router)))
    (ui-segment {:color "red" :inverted true}
      "Failed to load record")))

(def ui-show (comp/factory Show))

(defsc IndexPage
  [_this {:ui/keys [report]
          :as props}]
  {:ident         (fn [] [::m.navlinks/id index-page-key])
   :initial-state {::m.navlinks/id index-page-key
                   :ui/report      {}}
   :query         [::m.navlinks/id
                   {:ui/report (comp/get-query Report)}]
   :route-segment ["relays"]
   :will-enter    (u.loader/page-loader index-page-key)}
  (log/debug :IndexPage/starting {:props props})
  (dom/div {}
    (ui-report report)))

(defsc ShowPage
  [_this {id                model-key
          ::m.navlinks/keys [target]
          :as               props}]
  {:ident         (fn [] [::m.navlinks/id show-page-key])
   :initial-state (fn [_props]
                    {model-key           nil
                     ::m.navlinks/id     show-page-key
                     ::m.navlinks/target {}})
   :query         (fn [_props]
                    [model-key
                     ::m.navlinks/id
                     {::m.navlinks/target (comp/get-query Show)}])
   :route-segment ["relay" :id]
   :will-enter    (u.loader/targeted-router-loader show-page-key model-key ::ShowPage)}
  (log/debug :ShowPage/starting {:props props})
  (if (and target id)
    (ui-show target)
    (ui-segment {:color "red" :inverted true}
      "Failed to load page")))
