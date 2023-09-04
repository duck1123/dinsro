(ns dinsro.ui.admin.models
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.models :as j.models]
   [dinsro.model.models :as m.models]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogc :as log]))

;; [[../../model/models.cljc]]

(def index-page-id :admin-models)
(def model-key ::m.models/id)
(def parent-router-id :admin)
(def required-role :admin)
(def show-page-id :admin-models-show)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters   {}
   ro/columns             []
   ro/control-layout      {:action-buttons [::refresh]}
   ro/controls            {::refresh u.links/refresh-control}
   ro/row-pk              m.models/id
   ro/run-on-mount?       true
   ro/source-attribute    ::j.models/index
   ro/title               "Models"})

(def ui-report (comp/factory Report))

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
   :route-segment     ["models"]}
  (log/trace :Page/starting {:props props})
  (ui-report report))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::IndexPage
   o.navlinks/description   "Admin index Models"
   o.navlinks/label         "Models"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})

(m.navlinks/defroute show-page-id
  {o.navlinks/control       ::ShowPage
   o.navlinks/description   "Admin show page for models"
   o.navlinks/label         "Show Model"
   o.navlinks/input-key     model-key
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    index-page-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
