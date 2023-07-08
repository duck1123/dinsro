(ns dinsro.ui.admin.core.words
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.core.words :as j.c.words]
   [dinsro.model.core.words :as m.c.words]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../joins/core/words.cljc]]
;; [[../../../model/core/words.cljc]]

(def index-page-key :admin-core-words)
(def model-key ::m.c.words/id)
(def show-page-key :admin-core-words-show)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.c.words/wallet #(u.links/ui-wallet-link %2)}
   ro/columns           [m.c.words/word
                         m.c.words/position
                         m.c.words/mnemonic]
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.c.words/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.words/index
   ro/title             "Word Report"})

(def ui-report (comp/factory Report))

(defsc Show
  [_this {::m.c.words/keys [id word]
          :as              props}]
  {:ident         ::m.c.words/id
   :initial-state (fn [props]
                    (let [id (model-key props)]
                      {model-key        id
                       ::m.c.words/word ""}))
   :pre-merge     (u.loader/page-merger model-key {})
   :query         [::m.c.words/id
                   ::m.c.words/word]}
  (log/info :Show/starting {:props props})
  (if id
    (dom/div {}
      (ui-segment {}
        (dom/dl {}
          (dom/dt {} "word")
          (dom/dd {} (str word)))))
    (u.debug/load-error props "admin show word record")))

(def ui-show (comp/factory Show))

(defsc IndexPage
  [_this {:ui/keys [report]}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["words"]
   :will-enter        (u.loader/page-loader index-page-key)}
  (dom/div {}
    (ui-report report)))

(defsc ShowPage
  [_this {::m.c.words/keys  [id]
          ::m.navlinks/keys [target]
          :as               props}]
  {:ident         (fn [] [::m.navlinks/id show-page-key])
   :initial-state {::m.c.words/id      nil
                   ::m.navlinks/id     show-page-key
                   ::m.navlinks/target {}}
   :query         [::m.navlinks/id
                   ::m.c.words/id
                   {::m.navlinks/target (comp/get-query Show)}]
   :route-segment ["word" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-key model-key ::ShowPage)}
  (log/info :ShowPage/starting {:props props})
  (if (and target id)
    (ui-show target)
    (u.debug/load-error props "admin show word")))
