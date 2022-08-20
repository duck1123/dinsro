(ns dinsro.ui.core.wallet-words
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.core.words :as m.c.words]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogc :as log]))

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.c.words/word
                        m.c.words/position]
   ro/controls         {::refresh u.links/refresh-control}
   ro/control-layout   {:action-buttons [::refresh]}
   ro/field-formatters {::m.c.words/wallet #(u.links/ui-wallet-link %2)}
   ro/route            "wallet-words"
   ro/row-pk           m.c.words/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.c.words/index
   ro/title            "Words"})

(def ui-wallet-words-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report] :as props}]
  {:query             [::m.c.wallets/id
                       {:ui/report (comp/get-query Report)}]
   :componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :initial-state     {::m.c.wallets/id nil
                       :ui/report       {}}
   :ident             (fn [] [:component/id ::SubPage])}
  (log/finer :SubPage/creating {:props props})
  (ui-wallet-words-report report))

(def ui-wallet-words-sub-page (comp/factory SubPage))
