(ns dinsro.ui.admin.navlinks
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.reports.admin.navlinks :as u.r.a.navlinks]
   [lambdaisland.glogc :as log]))

(def index-page-id :admin-navlinks)
(def model-key o.navlinks/id)
(def parent-router-id :admin)
(def required-role :admin)
(def show-page-id :admin-navlinks-show)

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
          :as      props}]
  {:componentDidMount #(report/start-report! % u.r.a.navlinks/Report {})
   :ident             (fn [_] [o.navlinks/id index-page-id])
   :initial-state     (fn [_props]
                        {o.navlinks/id index-page-id
                         :ui/report    (comp/get-initial-state u.r.a.navlinks/Report {})})
   :query             (fn []
                        [o.navlinks/id
                         {:ui/report (comp/get-query u.r.a.navlinks/Report)}])
   :route-segment     ["navlinks"]}
  (log/trace :Page/starting {:props props})
  (u.r.a.navlinks/ui-report report))

(defsc ShowPage
  [_this props]
  {:ident         (fn [] [o.navlinks/id show-page-id])
   :initial-state (fn [props]
                    {model-key         (model-key props)
                     o.navlinks/id     show-page-id
                     o.navlinks/target (comp/get-initial-state Show {})})
   :query         (fn []
                    [o.navlinks/id
                     {o.navlinks/target (comp/get-query Show)}])
   :route-segment ["currency" :id]
   :will-enter    (u.loader/targeted-router-loader show-page-id model-key ::ShowPage)}
  (u.loader/show-page props model-key ui-show))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::IndexPage
   o.navlinks/description   "Admin index navlinks"
   o.navlinks/label         "Navlinks"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})

(m.navlinks/defroute show-page-id
  {o.navlinks/control       ::ShowPage
   o.navlinks/description   "Admin show page for navlink"
   o.navlinks/label         "Show Navlink"
   o.navlinks/input-key     model-key
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    index-page-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
