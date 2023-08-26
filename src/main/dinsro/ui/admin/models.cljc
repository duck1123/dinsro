(ns dinsro.ui.admin.models
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.models :as j.models]
   [dinsro.model.models :as m.models]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogc :as log]))

;; [[../../model/models.cljc]]

(def index-page-id :admin-models)
(def model-key ::m.models/id)
(def parent-router-id :admin)
(def required-role :admin)
(def show-page-key :admin-models-show)

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
   :ident             (fn [_] [::m.navlinks/id index-page-id])
   :initial-state     {::m.navlinks/id index-page-id
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["models"]}
  (log/trace :Page/starting {:props props})
  (ui-report report))

(m.navlinks/defroute index-page-id
  {::m.navlinks/control       ::IndexPage
   ::m.navlinks/description   "Admin index Models"
   ::m.navlinks/label         "Models"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    parent-router-id
   ::m.navlinks/router        parent-router-id
   ::m.navlinks/required-role required-role})

(m.navlinks/defroute show-page-key
  {::m.navlinks/control       ::ShowPage
   ::m.navlinks/description   "Admin show page for models"
   ::m.navlinks/label         "Show Model"
   ::m.navlinks/input-key     model-key
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    index-page-id
   ::m.navlinks/router        parent-router-id
   ::m.navlinks/required-role required-role})
