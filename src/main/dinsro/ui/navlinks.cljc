(ns dinsro.ui.navlinks
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.navlinks :as j.navlinks]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../joins/navlinks.cljc]]
;; [[../model/navlinks.cljc]]
;; [[../mutations/navlinks.cljc]]

(def index-page-id :navlinks)
(def model-key o.navlinks/id)
(def parent-router :root)
(def required-role :user)
(def show-page-key :navlinks-show)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters   {o.navlinks/id           #(str %2)
                           o.navlinks/control      #(str %2)
                           o.navlinks/router       #(and %2 (u.links/ui-navbar-link %2))
                           o.navlinks/model-key    #(str %2)
                           o.navlinks/navigate-key #(str %2)
                           o.navlinks/parent-key   #(str %2)
                           o.navlinks/input-key    #(str %2)}
   ro/columns             [m.navlinks/id
                           m.navlinks/parent-key
                           m.navlinks/control
                           m.navlinks/label
                           m.navlinks/description
                           m.navlinks/navigate-key
                           m.navlinks/input-key
                           m.navlinks/required-role]
   ro/control-layout      {:action-buttons [::refresh]}
   ro/controls            {::refresh u.links/refresh-control}
   ro/initial-sort-params {:sort-by          o.navlinks/control
                           :sortable-columns #{o.navlinks/label
                                               o.navlinks/parent-key
                                               o.navlinks/control}
                           :ascending?       false}
   ro/row-pk              m.navlinks/id
   ro/run-on-mount?       true
   ro/source-attribute    ::j.navlinks/index
   ro/title               "Navlinks"})

(def ui-report (comp/factory Report))

(defsc Show
  [_this {id    o.navlinks/id
          label o.navlinks/label
          :as   props}]
  {:ident         ::m.navlinks/id
   :initial-state (fn [props]
                    {o.navlinks/id    (o.navlinks/id props)
                     o.navlinks/label ""})
   :pre-merge     (u.loader/page-merger model-key {})
   :query         (fn []
                    [o.navlinks/id
                     o.navlinks/label])}
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
   :ident             (fn [_] [o.navlinks/id index-page-id])
   :initial-state     (fn [_props]
                        {o.navlinks/id index-page-id
                         :ui/report      (comp/get-initial-state Report {})})
   :query             (fn []
                        [o.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["navlinks"]}
  (log/trace :Page/starting {:props props})
  (ui-report report))

(defsc ShowPage
  [_this {id     o.navlinks/id
          target o.navlinks/target
          :as    props}]
  {:ident         (fn [] [o.navlinks/id show-page-key])
   :initial-state (fn [props]
                    {model-key         (model-key props)
                     o.navlinks/id     show-page-key
                     o.navlinks/target (comp/get-initial-state Show {})})
   :query         (fn []
                    [o.navlinks/id
                     {o.navlinks/target (comp/get-query Show)}])
   :route-segment ["currency" :id]
   :will-enter    (u.loader/targeted-router-loader show-page-key model-key ::ShowPage)}
  (log/info :ShowPage/starting {:props props})
  (if (and target id)
    (ui-show target)
    (u.debug/load-error props "show navlink page")))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::IndexPage
   o.navlinks/label         "Navlinks"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router
   o.navlinks/router        parent-router
   o.navlinks/required-role required-role})
