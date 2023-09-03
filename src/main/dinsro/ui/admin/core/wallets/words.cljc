(ns dinsro.ui.admin.core.wallets.words
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid :refer [ui-grid]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid-column :refer [ui-grid-column]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid-row :refer [ui-grid-row]]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.core.words :as j.c.words]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.core.words :as m.c.words]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.options.core.wallets :as o.c.wallets]
   [dinsro.options.core.words :as o.c.words]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../../joins/core/words.cljc]]
;; [[../../../../model/core/words.cljc]]

(def index-page-id :admin-core-wallets-show-words)
(def model-key o.c.words/id)
(def parent-model-key o.c.wallets/id)
(def parent-router-id :admin-core-wallets-show)
(def required-role :admin)
(def router-key :dinsro.ui.admin.core.wallets/Router)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {o.c.words/wallet #(u.links/ui-admin-wallet-link %2)}
   ro/columns           [m.c.words/word
                         m.c.words/position]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {parent-model-key {:type :uuid :label "id"}
                         ::refresh        u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.c.words/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.words/admin-index
   ro/title             "Words"})

(defsc SubPage
  [_this {::m.c.wallets/keys [id]
          :ui/keys           [report]
          :as                props}]
  {:componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :ident             (fn [] [o.navlinks/id index-page-id])
   :initial-state     (fn [props]
                        {parent-model-key (parent-model-key props)
                         o.navlinks/id  index-page-id
                         :ui/report       (comp/get-initial-state Report {})})
   :query             (fn []
                        [[::dr/id router-key]
                         parent-model-key
                         o.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :will-enter        (u.loader/targeted-subpage-loader index-page-id parent-model-key ::SubPage)}
  (log/info :SubPage/starting {:props props})
  (if (and report id)
    (let [{:ui/keys [current-rows]} report
          sorted-rows               (sort-by o.c.words/position current-rows)
          groups                    (partition 12 sorted-rows)]
      (ui-grid {}
        (ui-grid-row {}
          (if (seq groups)
            (map (fn [words]
                   (ui-grid-column {:width 8}
                     (map
                      (fn [row]
                        (let [{position o.c.words/position
                               word o.c.words/word} row]
                          (dom/div :.eight.wide.column (str position) ". " (str word))))
                      words)))
                 groups)
            (ui-grid-column {:width 16}
              (ui-segment {}
                "No words"))))))
    (u.debug/load-error props "admin wallet words page")))

(def ui-sub-page (comp/factory SubPage))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::SubPage
   o.navlinks/input-key     parent-model-key
   o.navlinks/label         "Wallets"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
