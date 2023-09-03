(ns dinsro.ui.admin.core.mnemonics
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.core.mnemonics :as j.c.mnemonics]
   [dinsro.model.core.mnemonics :as m.c.mnemonics]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.options.core.mnemonics :as o.c.mnemonics]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../joins/core/mnemonics.cljc]]
;; [[../../../model/core/mnemonics.cljc]]
;; [[../../../ui/core/mnemonics.cljs]]

(def index-page-id :admin-core-mnemonics)
(def model-key o.c.mnemonics/id)
(def parent-router-id :admin-core)
(def required-role :admin)
(def show-page-key :admin-core-mnemonics-show)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {o.c.mnemonics/user #(u.links/ui-user-link %2)}
   ro/columns           [m.c.mnemonics/name
                         m.c.mnemonics/entropy
                         m.c.mnemonics/user]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.c.mnemonics/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.mnemonics/index
   ro/title             "Mnemonics"})

(def ui-report (comp/factory Report))

(defsc Show
  [_this {id o.c.mnemonics/id
          entropy o.c.mnemonics/entropy
          :as                  props}]
  {:ident         ::m.c.mnemonics/id
   :initial-state (fn [props]
                    {model-key               (model-key props)
                     o.c.mnemonics/entropy ""})
   :pre-merge     (u.loader/page-merger model-key {})
   :query         (fn []
                    [o.c.mnemonics/id
                     o.c.mnemonics/entropy])}
  (log/info :Show/starting {:props props})
  (if id
    (dom/div {}
      (ui-segment {}
        (dom/dl {}
          (dom/dt {} "Entropy")
          (dom/dd {} (str entropy)))))
    (u.debug/load-error props "admin show mnemonic record")))

(def ui-show (comp/factory Show))

(defsc IndexPage
  [_this {report :ui/report}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [o.navlinks/id index-page-id])
   :initial-state     (fn [_props]
                        {o.navlinks/id index-page-id
                         :ui/report      (comp/get-initial-state Report {})})
   :query             (fn []
                        [o.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["mnemonics"]
   :will-enter        (u.loader/page-loader index-page-id)}
  (dom/div {}
    (ui-report report)))

(defsc ShowPage
  [_this props]
  {:ident         (fn [] [o.navlinks/id show-page-key])
   :initial-state (fn [props]
                    {model-key (model-key props)
                     o.navlinks/id     show-page-key
                     o.navlinks/target (comp/get-initial-state Show {})})
   :query         (fn []
                    [o.c.mnemonics/id
                     o.navlinks/id
                     {o.navlinks/target (comp/get-query Show)}])
   :route-segment ["mnemonic" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-key model-key ::ShowPage)}
  (u.loader/show-page props model-key ui-show))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::IndexPage
   o.navlinks/description   "Admin Index Mnemonics"
   o.navlinks/label         "Mnemonics"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})

(m.navlinks/defroute show-page-key
  {o.navlinks/control       ::ShowPage
   o.navlinks/description   "Admin show mnemonic"
   o.navlinks/input-key     model-key
   o.navlinks/label         "Mnemonics"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    index-page-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
