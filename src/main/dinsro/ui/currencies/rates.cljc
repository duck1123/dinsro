(ns dinsro.ui.currencies.rates
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [dinsro.model.navlinks :as m.navlinks :refer [defroute]]
   [dinsro.options.currencies :as o.currencies]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.options.rates :as o.rates]
   [dinsro.ui.controls :as u.controls]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.reports.currencies.rates :as u.r.c.rates]))

;; [[../../joins/rates.cljc]]
;; [[../../model/rates.cljc]]
;; [[../../ui/admin/rates.cljc]]

(def index-page-id :currencies-show-rates)
(def model-key o.rates/id)
(def parent-model-key o.currencies/id)
(def parent-router-id :currencies-show)
(def required-role :user)
(def router-key :dinsro.ui.currencies/Router)

(defsc SubPage
  [_this props]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key u.r.c.rates/Report)
   :ident             (fn [] [o.navlinks/id index-page-id])
   :initial-state     (fn [props]
                        {parent-model-key (parent-model-key props)
                         o.navlinks/id    index-page-id
                         :ui/report       (comp/get-initial-state u.r.c.rates/Report {})})
   :query             (fn []
                        [[::dr/id router-key]
                         parent-model-key
                         o.navlinks/id
                         {:ui/report (comp/get-query u.r.c.rates/Report)}])
   :route-segment     ["rates"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-id parent-model-key ::SubPage)}
  (u.controls/sub-page-report-loader props u.r.c.rates/ui-report parent-model-key :ui/report))

(defroute index-page-id
  {o.navlinks/control       ::SubPage
   o.navlinks/label         "Rates"
   o.navlinks/input-key     parent-model-key
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
