(ns dinsro.ui.admin.nostr.filter-items
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.nostr.filter-items :as j.n.filter-items]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.filter-items :as m.n.filter-items]
   [dinsro.mutations.nostr.filter-items :as mu.n.filter-items]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../joins/nostr/filter_items.cljc]]
;; [[../../../model/nostr/filter_items.cljc]]
;; [[../../../mutations/nostr/filter_items.cljc]]
;; [[../../../queries/nostr/filter_items.clj]]
;; [[../../../ui/admin/nostr.cljc]]

(def index-page-id :admin-nostr-filter-items)
(def model-key ::m.n.filter-items/id)
(def parent-router-id :admin-nostr)
(def required-role :admin)
(def show-page-key :admin-nostr-filter-items-show)
(def debug-props false)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.n.filter-items/delete!))

(defsc FilterItemItem
  [_this {::j.n.filter-items/keys [query-string]
          :as                     props}]
  {:ident         ::m.n.filter-items/id
   :initial-state {::m.n.filter-items/id           nil
                   ::j.n.filter-items/query-string ""}
   :query         [::m.n.filter-items/id
                   ::j.n.filter-items/query-string]}
  (ui-segment {}
    (dom/div {} (str query-string))
    (when debug-props
      (u.debug/log-props props))))

(def ui-filter-item-item (comp/factory FilterItemItem {:keyfn ::m.n.filter-items/id}))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.filter-items/filter  #(u.links/ui-admin-filter-link %2)
                         ::m.n.filter-items/pubkey  #(and %2 (u.links/ui-admin-pubkey-link %2))
                         ::j.n.filter-items/request #(and %2 (u.links/ui-admin-request-link %2))}
   ro/columns           [m.n.filter-items/filter
                         m.n.filter-items/index
                         m.n.filter-items/kind
                         m.n.filter-items/type
                         m.n.filter-items/event
                         m.n.filter-items/pubkey
                         j.n.filter-items/request
                         j.n.filter-items/query-string]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [delete-action]
   ro/row-pk            m.n.filter-items/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.filter-items/index
   ro/title             "Filter Items"})

(def ui-report (comp/factory Report))

(defsc Show
  [_this {::m.n.filter-items/keys [id]
          :as                     props}]
  {:ident         ::m.n.filter-items/id
   :initial-state (fn [props]
                    (let [id (model-key props)]
                      {model-key id}))
   :query         [::m.n.filter-items/id]}
  (log/info :Show/starting {:props props})
  (if id
    (ui-segment {} "TODO: Show filter item")
    (u.debug/load-error props "admin filter items record")))

(def ui-show (comp/factory Show))

(defsc IndexPage
  [_this {:ui/keys [report]}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [::m.navlinks/id index-page-id])
   :initial-state     {::m.navlinks/id index-page-id
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["filter-items"]
   :will-enter        (u.loader/page-loader index-page-id)}
  (dom/div {}
    (ui-report report)))

(defsc ShowPage
  [_this {::m.n.filter-items/keys [id]
          ::m.navlinks/keys       [target]
          :as                     props}]
  {:ident         (fn [] [::m.navlinks/id show-page-key])
   :initial-state {::m.n.filter-items/id nil
                   ::m.navlinks/id       show-page-key
                   ::m.navlinks/target   {}}
   :query         [::m.n.filter-items/id
                   ::m.navlinks/id
                   {::m.navlinks/target (comp/get-query Show)}]
   :route-segment ["filter-item" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-key model-key ::ShowPage)}
  (log/info :ShowPage/starting {:props props})
  (if (and target id)
    (ui-show target)
    (u.debug/load-error props "admin filter items")))

(m.navlinks/defroute index-page-id
  {::m.navlinks/control       ::IndexPage
   ::m.navlinks/label         "Items"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    parent-router-id
   ::m.navlinks/router        parent-router-id
   ::m.navlinks/required-role required-role})

(m.navlinks/defroute show-page-key
  {::m.navlinks/control       ::ShowPage
   ::m.navlinks/input-key     model-key
   ::m.navlinks/label         "Items"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    index-page-id
   ::m.navlinks/router        parent-router-id
   ::m.navlinks/required-role required-role})
