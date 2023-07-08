(ns dinsro.ui.core.mnemonics
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.core.mnemonics :as j.c.mnemonics]
   [dinsro.model.core.mnemonics :as m.c.mnemonics]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../joins/core/mnemonics.cljc]]
;; [[../../model/core/mnemonics.cljc]]
;; [[../../ui/admin/core/mnemonics.cljs]]

(def index-page-key :core-mnemonics)
(def model-key ::m.c.mnemonics/id)
(def show-page-key :core-mnemonics-show)

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.c.mnemonics/name
                        m.c.mnemonics/entropy
                        m.c.mnemonics/user]
   ro/field-formatters {::m.c.mnemonics/user #(u.links/ui-user-link %2)}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/route            "mnemonics"
   ro/row-pk           m.c.mnemonics/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.c.mnemonics/index
   ro/title            "Mnemonics"})

(def ui-report (comp/factory Report))

(defsc Show
  [_this {::m.c.mnemonics/keys [id entropy]
          :as                  props}]
  {:ident         ::m.c.mnemonics/id
   :initial-state (fn [props]
                    (let [id (model-key props)]
                      {model-key               id
                       ::m.c.mnemonics/entorpy ""}))
   :pre-merge     (u.loader/page-merger model-key {})
   :query         [::m.c.mnemonics/id
                   ::m.c.mnemonics/entropy]}
  (log/info :Show/starting {:props props})
  (if id
    (dom/div {}
      (ui-segment {}
        (dom/dl {}
          (dom/dt {} "Entropy")
          (dom/dd {} (str entropy)))))
    (u.debug/load-error props "core show mnemonic record")))

(def ui-show (comp/factory Show))

(defsc IndexPage
  [_this {:ui/keys [report]
          :as      props}]
  {:ident         (fn [] [::m.navlinks/id index-page-key])
   :initial-state {::m.navlinks/id index-page-key
                   :ui/report      {}}
   :query         [::m.navlinks/id
                   {:ui/report (comp/get-query Report)}]
   :route-segment ["categories"]
   :will-enter    (u.loader/page-loader index-page-key)}
  (log/info :IndexPage/starting {:props props})
  (dom/div {}
    (ui-report report)))

(defsc ShowPage
  [_this {::m.c.mnemonics/keys [id]
          ::m.navlinks/keys [target]
          :as               props}]
  {:ident         (fn [] [::m.navlinks/id show-page-key])
   :initial-state {::m.c.mnemonics/id nil
                   ::m.navlinks/id     show-page-key
                   ::m.navlinks/target {}}
   :query         [::m.c.mnemonics/id
                   ::m.navlinks/id
                   {::m.navlinks/target (comp/get-query Show)}]
   :route-segment ["mnemonic" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-key model-key ::ShowPage)}
  (log/info :ShowPage/starting {:props props})
  (if (and target id)
    (ui-show target)
    (u.debug/load-error props "core show mnemonic")))
