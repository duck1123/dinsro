(ns dinsro.ui.admin.models
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   ;; #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   ;; #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   ;; [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.models :as j.models]
   ;; [dinsro.joins.navlinks :as j.navlinks]
   [dinsro.model.models :as m.models]
   [dinsro.model.navlinks :as m.navlinks]
   ;; [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   ;; [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../model/models.cljc]]

(def index-page-key :admin-models)
(def model-key ::m.models/id)
(def parent-router-id :admin)
(def show-page-key :admin-models-show)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters   {}
   ro/columns             []
   ro/control-layout      {:action-buttons [::refresh]}
   ro/controls            {::refresh u.links/refresh-control}
   ;; ro/initial-sort-params {:sort-by          ::m.navlinks/control
   ;;                         :sortable-columns #{::m.navlinks/label
   ;;                                             ::m.navlinks/parent-key
   ;;                                             ::m.navlinks/control}
   ;;                         :ascending?       false}
   ro/row-pk              m.models/id
   ro/run-on-mount?       true
   ro/source-attribute    ::j.models/index
   ro/title               "Models"})

(def ui-report (comp/factory Report))

(defsc IndexPage
  [_this {:ui/keys [report]
          :as props}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [_] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["models"]}
  (log/trace :Page/starting {:props props})
  (ui-report report))

(m.navlinks/defroute index-page-key
  {::m.navlinks/control       ::IndexPage
   ::m.navlinks/description   "Admin index Models"
   ::m.navlinks/label         "Models"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    :admin
   ::m.navlinks/router        parent-router-id
   ::m.navlinks/required-role :admin})

(m.navlinks/defroute show-page-key
  {::m.navlinks/control       ::ShowPage
   ::m.navlinks/description   "Admin show page for models"
   ::m.navlinks/label         "Show Model"
   ::m.navlinks/input-key     model-key
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    index-page-key
   ::m.navlinks/router        :admin
   ::m.navlinks/required-role :admin})
