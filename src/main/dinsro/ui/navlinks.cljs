(ns dinsro.ui.navlinks
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.navlinks :as j.navlinks]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../joins/navlinks.cljc]]
;; [[../model/navlinks.cljc]]
;; [[../mutations/navlinks.cljc]]

(def index-page-key :navlinks)
(def model-key ::m.navlinks/id)
(def show-page-key :navlinks-show)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters   {::m.navlinks/id         #(str %2)
                           ::m.navlinks/control    #(str %2)
                           ::m.navlinks/route      #(str %2)
                           ::m.navlinks/router     #(and %2 (u.links/ui-navbar-link %2))
                           ::m.navlinks/model-key  #(str %2)
                           ::m.navlinks/parent-key #(str %2)
                           ::m.navlinks/input-key  #(str %2)}
   ro/columns             [m.navlinks/id
                           m.navlinks/parent-key
                           m.navlinks/control
                           m.navlinks/label
                           m.navlinks/description
                           ;; m.navlinks/router
                           ;; m.navlinks/route
                           ;; m.navlinks/model-key
                           m.navlinks/navigate-key
                           m.navlinks/input-key
                           ;; m.navlinks/auth-link?
                           m.navlinks/required-role]
   ro/control-layout      {:action-buttons [::refresh]}
   ro/controls            {::refresh u.links/refresh-control}
   ro/initial-sort-params {:sort-by          ::m.navlinks/control
                           :sortable-columns #{::m.navlinks/label
                                               ::m.navlinks/parent-key
                                               ::m.navlinks/control}
                           :ascending?       false}
   ro/row-pk              m.navlinks/id
   ro/run-on-mount?       true
   ro/source-attribute    ::j.navlinks/index
   ro/title               "Navlinks"})

(def ui-report (comp/factory Report))

(defsc Show
  [_this {::m.navlinks/keys [id label]
          :as                 props}]
  {:ident         ::m.navlinks/id
   :initial-state (fn [props]
                    (let [id (::m.navlinks/id props)]
                      {::m.navlinks/id id
                       ::m.navlinks/label ""}))
   :pre-merge     (u.loader/page-merger ::m.navlinks/id {})
   :query         [::m.navlinks/id
                   ::m.navlinks/label]}
  (if id
    (dom/div {}
      (ui-segment {}
        (dom/h1 {} (str label))))
    (u.debug/load-error props "Show navlink record")))

(def ui-show (comp/factory Show))

(defsc IndexPage
  [_this {:ui/keys [report]
          :as props}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [_] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["navlinks"]}
  (log/trace :Page/starting {:props props})
  (ui-report report))

(defsc ShowPage
  [_this {::m.navlinks/keys   [id target]
          :as                 props}]
  {:ident         (fn [] [::m.navlinks/id show-page-key])
   :initial-state {::m.navlinks/id     show-page-key
                   ::m.navlinks/target {}}
   :query         [::m.navlinks/id
                   {::m.navlinks/target (comp/get-query Show)}]
   :route-segment ["currency" :id]
   :will-enter    (u.loader/targeted-router-loader show-page-key model-key ::ShowPage)}
  (log/info :ShowPage/starting {:props props})
  (if (and target id)
    (ui-show target)
    (u.debug/load-error props "show navlink page")))
