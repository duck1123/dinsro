(ns dinsro.ui.admin.nostr.witnesses
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.nostr.witnesses :as j.n.witnesses]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.witnesses :as m.n.witnesses]
   [dinsro.mutations.nostr.witnesses :as mu.n.witnesses]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../actions/nostr/witnesses.clj]]
;; [[../../../joins/nostr/witnesses.cljc]]
;; [[../../../model/nostr/witnesses.cljc]]
;; [[../../../mutations/nostr/witnesses.cljc]]
;; [[../../../ui/admin/nostr/runs/witnesses.cljc]]
;; [[../../../ui/nostr/events/witnesses.cljc]]

(def index-page-key :admin-nostr-witnesses)
(def model-key ::m.n.witnesses/id)
(def show-page-key :admin-nostr-witnesses-show)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.n.witnesses/delete!))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.witnesses/id #(u.links/ui-admin-witness-link %3)
                         ::m.n.witnesses/event #(when %2 (u.links/ui-admin-event-link %2))
                         ::m.n.witnesses/relay #(when %2 (u.links/ui-admin-relay-link %2))}
   ro/columns           [m.n.witnesses/id
                         m.n.witnesses/event
                         m.n.witnesses/relay]
   ro/control-layout    {:action-buttons [::new ::refresh]}
   ro/controls          {::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [delete-action]
   ro/row-pk            m.n.witnesses/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.witnesses/admin-index
   ro/title             "Witnesses"})

(def ui-report (comp/factory Report))

(defsc Show
  [_this {::m.n.witnesses/keys [id event relay]
          :as                  props}]
  {:ident         ::m.n.witnesses/id
   :initial-state (fn [props]
                    (let [id (model-key props)]
                      {model-key             id
                       ::m.n.witnesses/event {}
                       ::m.n.witnesses/relay {}}))
   :query         [::m.n.witnesses/id
                   ::m.n.witnesses/event
                   {::m.n.witnesses/relay (comp/get-query u.links/AdminRelayLinkForm)}]}
  (log/info :Show/starting {:props props})
  (if id
    (ui-segment {}
      (dom/div {} (str id))
      (dom/div {}
        (when event
          (u.links/ui-admin-event-link event)))
      (dom/div {}
        (when relay
          (dom/div {}
            (u.debug/log-props relay)
            (comment (u.links/ui-admin-relay-link relay)))))
      (u.debug/log-props props))
    (u.debug/load-error props "admin show witness")))

(def ui-show (comp/factory Show))

(defsc IndexPage
  [_this {:ui/keys [report]}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["witnesses"]
   :will-enter        (u.loader/page-loader index-page-key)}
  (dom/div {}
    (ui-report report)))

(defsc ShowPage
  [_this {::m.navlinks/keys [target]
          :as               props}]
  {:ident         (fn [] [::m.navlinks/id show-page-key])
   :initial-state (fn [props]
                    {model-key           (model-key props)
                     ::m.navlinks/id     show-page-key
                     ::m.navlinks/target (comp/get-initial-state Show {})})
   :query         (fn []
                    [model-key
                     ::m.navlinks/id
                     {::m.navlinks/target (comp/get-query Show)}])
   :route-segment ["witness" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-key model-key ::ShowPage)}
  (log/info :ShowPage/starting {:props props})
  (if (model-key props)
    (if target
      (ui-show target)
      (u.debug/load-error props "admin show witness target"))
    (u.debug/load-error props "admin show witness page")))

(m.navlinks/defroute index-page-key
  {::m.navlinks/control       ::IndexPage
   ::m.navlinks/label         "Witnesses"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    :admin-nostr
   ::m.navlinks/router        :admin-nostr
   ::m.navlinks/required-role :admin})

(m.navlinks/defroute show-page-key
  {::m.navlinks/control       ::ShowPage
   ::m.navlinks/input-key     model-key
   ::m.navlinks/label         "Show Witness"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    index-page-key
   ::m.navlinks/router        :admin-nostr
   ::m.navlinks/required-role :admin})
