(ns dinsro.ui.admin.core.wallets.words
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.core.words :as j.c.words]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.core.words :as m.c.words]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../../joins/core/words.cljc]]
;; [[../../../../model/core/words.cljc]]

(def index-page-key :admin-core-wallets-show-words)
(def model-key ::m.c.words/id)
(def parent-model-key ::m.c.wallets/id)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.c.words/wallet #(u.links/ui-admin-wallet-link %2)}
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
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.c.wallets/id nil
                       ::m.navlinks/id  index-page-key
                       :ui/report       {}}
   :query             [::m.c.wallets/id
                       ::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)}
  (log/info :SubPage/starting {:props props})
  (if (and report id)
    (let [{:ui/keys [current-rows]} report
          sorted-rows               (sort-by ::m.c.words/position current-rows)
          groups                    (partition 12 sorted-rows)]
      (dom/div {}
        (dom/div :.ui.grid
          (if (seq groups)
            (map (fn [words]
                   (dom/div :.eight.wide.column
                     (map
                      (fn [row]
                        (let [{::m.c.words/keys [position word]} row]
                          (dom/div :.eight.wide.column (str position) ". " (str word))))
                      words)))
                 groups)
            (ui-segment {}
              "No words")))))
    (u.debug/load-error props "admin wallet words page")))

(def ui-sub-page (comp/factory SubPage))
