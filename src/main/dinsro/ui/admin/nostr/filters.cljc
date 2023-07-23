(ns dinsro.ui.admin.nostr.filters
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.nostr.filters :as j.n.filters]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.filters :as m.n.filters]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../joins/nostr/filters.cljc]]
;; [[../../../model/nostr/filters.cljc]]

(def index-page-key :admin-nostr-filters)
(def model-key ::m.n.filters/id)
(def show-page-key :admin-nostr-filters-show)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.filters/request #(u.links/ui-admin-request-link %2)
                         ::m.n.filters/index   #(u.links/ui-admin-filter-link %3)}
   ro/columns           [m.n.filters/index
                         m.n.filters/request
                         m.n.filters/since
                         m.n.filters/until
                         j.n.filters/item-count]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.n.filters/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.filters/admin-index
   ro/title             "Filters"})

(def ui-report (comp/factory Report))

(defsc Show
  [_this {::m.n.filters/keys [id]
          :as                props}]
  {:ident         ::m.n.filters/id
   :initial-state (fn [props]
                    (let [id (model-key props)]
                      {model-key id}))
   :query         [::m.n.filters/id]}
  (log/info :Show/starting {:props props})
  (if id
    (ui-segment {:color "yellow" :inverted true}
      "TODO: Show event")
    (u.debug/load-error props "admin filters record")))

(def ui-show (comp/factory Show))

(defsc IndexPage
  [_this {:ui/keys [report]
          :as props}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident         (fn [] [::m.navlinks/id index-page-key])
   :initial-state {::m.navlinks/id index-page-key
                   :ui/report      {}}
   :query         [::m.navlinks/id
                   {:ui/report (comp/get-query Report)}]
   :route-segment ["filters"]
   :will-enter    (u.loader/page-loader index-page-key)}
  (log/info :IndexPage/starting {:props props})
  (dom/div {}
    (ui-report report)))

(defsc ShowPage
  [_this {::m.n.filters/keys [id]
          ::m.navlinks/keys  [target]
          :as                props}]
  {:ident         (fn [] [::m.navlinks/id show-page-key])
   :initial-state {::m.n.filters/id    nil
                   ::m.navlinks/id     show-page-key
                   ::m.navlinks/target {}}
   :query         [::m.n.filters/id
                   ::m.navlinks/id
                   {::m.navlinks/target (comp/get-query Show)}]
   :route-segment ["filter" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-key model-key ::ShowPage)}
  (log/info :ShowPage/starting {:props props})
  (if (and target id)
    (ui-show target)
    (u.debug/load-error props "admin show nostr filter")))

(m.navlinks/defroute index-page-key
  {::m.navlinks/control       ::IndexPage
   ::m.navlinks/label         "Filters"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    :admin-nostr
   ::m.navlinks/router        :admin-nostr
   ::m.navlinks/required-role :admin})

(m.navlinks/defroute show-page-key
  {::m.navlinks/control       ::ShowPage
   ::m.navlinks/input-key     model-key
   ::m.navlinks/label         "Show Filter"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    index-page-key
   ::m.navlinks/router        :admin-nostr
   ::m.navlinks/required-role :admin})
