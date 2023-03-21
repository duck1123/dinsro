(ns dinsro.ui.core.wallets.words
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.core.words :as j.c.words]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.core.words :as m.c.words]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogc :as log]))

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.c.words/word
                        m.c.words/position]
   ro/controls         {::m.c.wallets/id {:type :uuid :label "id"}
                        ::refresh u.links/refresh-control}
   ro/control-layout   {:action-buttons [::refresh]}
   ro/field-formatters {::m.c.words/wallet #(u.links/ui-wallet-link %2)}
   ro/row-pk           m.c.words/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.c.words/index
   ro/title            "Words"})

(defsc SubPage
  [_this {:ui/keys [report] :as props}]
  {:query             [::m.c.wallets/id
                       {:ui/report (comp/get-query Report)}]
   :componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :initial-state     {::m.c.wallets/id nil
                       :ui/report       {}}
   :ident             (fn [] [:component/id ::SubPage])}
  (log/info :SubPage/creating {:props props})
  (let [{:ui/keys [current-rows]} report
        sorted-rows               (sort-by ::m.c.words/position current-rows)
        groups                    (partition 12 sorted-rows)]
    (dom/div {}
      (dom/div :.ui.grid
        (map (fn [words]
               (dom/div :.eight.wide.column
                 (map
                  (fn [row]
                    (let [{::m.c.words/keys [position word]} row]
                      (dom/div :.eight.wide.column (str position) ". " (str word))))
                  words)))
             groups)))))

(def ui-sub-page (comp/factory SubPage))
