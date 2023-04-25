(ns dinsro.ui.settings.rate-sources
  (:require
   ["victory" :as victory]
   [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.rate-sources :as j.rate-sources]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.mutations.rate-sources :as mu.rate-sources]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.rate-sources.accounts :as u.rs.accounts]
   [dinsro.ui.rate-sources.rates :as u.rs.rates]))

(def ui-victory-chart (interop/react-factory victory/VictoryChart))
(def ui-victory-line (interop/react-factory victory/VictoryLine))

(def run-button
  {:type   :button
   :local? true
   :label  "Run"
   :action
   (fn [this _key]
     (let [{::m.rate-sources/keys [id]} (comp/props this)]
       (comp/transact! this [(mu.rate-sources/run-query! {::m.rate-sources/id id})])))})

(form/defsc-form NewForm
  [_this _props]
  {fo/action-buttons (concat [::run] form/standard-action-buttons)
   fo/attributes     [m.rate-sources/name
                      m.rate-sources/url
                      m.rate-sources/active?
                      m.rate-sources/path]
   fo/cancel-route   ["new-rate-source"]
   fo/controls       (merge form/standard-controls {::run run-button})
   fo/id             m.rate-sources/id
   fo/route-prefix   "rate-source"
   fo/title          "New Rate Source"})

(defrouter Router
  [_this _props]
  {:router-targets
   [u.rs.accounts/SubPage
    u.rs.rates/SubPage]})

(def ui-router (comp/factory Router))

(def menu-items
  [{:key   "accounts"
    :name  "Accounts"
    :route "dinsro.ui.rate-sources.accounts/SubPage"}
   {:key   "rates"
    :name  "Rates"
    :route "dinsro.ui.rate-sources.rates/SubPage"}])

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.rate-sources/name
                        m.rate-sources/url
                        m.rate-sources/active?]
   ro/field-formatters {::m.rate-sources/currency #(u.links/ui-currency-link %2)
                        ::m.rate-sources/name     #(u.links/ui-rate-source-link %3)}
   ro/route            "rate-sources"
   ro/row-pk           m.rate-sources/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.rate-sources/index
   ro/title            "Rate Sources"})

(defsc RateChart
  [_this _props]
  {}
  (dom/div {}
    "Rate Chart"))

(defsc Show
  [_this {::m.rate-sources/keys [name url active currency id]
          :ui/keys              [router]}]
  {:ident         ::m.rate-sources/id
   :initial-state {::m.rate-sources/name       ""
                   ::m.rate-sources/id         nil
                   ::m.rate-sources/active     false
                   ::m.rate-sources/currency   {}
                   ::m.rate-sources/url        ""
                   :ui/router                  {}}
   :pre-merge     (u.links/page-merger ::m.rate-sources/id {:ui/router Router})
   :query         [::m.rate-sources/name
                   ::m.rate-sources/url
                   {::m.rate-sources/currency (comp/get-query u.links/CurrencyLinkForm)}
                   ::m.rate-sources/active
                   ::m.rate-sources/id
                   {:ui/router (comp/get-query Router)}]
   :route-segment ["rate-sources" :id]
   :will-enter    (partial u.links/page-loader ::m.rate-sources/id ::Show)}
  (let [width  400
        height 300]
    (comp/fragment
     (dom/div :.ui.segment
       (dom/a {:href "/rate-sources"} "Rate Sources"))
     (dom/div :.ui.segment
       (dom/h1 {} (str name))
       (dom/p {} "Url: " (str url))
       (dom/p {} "Active: " (str (boolean active)))
       (dom/p {} "Currency: " (u.links/ui-currency-link currency))
       ;; (dom/p {} "Rate Count: " (str rate-count))
       (dom/div {:style {:width  (str width "px")
                         :height (str height "px")}}
         (let [value [;; {:date "2021-10-09T11:06:37" :rate 1813}
                      ;; {:date "2021-10-09T18:01:20" :rate 1823}
                      ;; {:date "2021-10-10T07:41:47" :rate 1812}
                      ;; {:date "2021-10-10T08:35:22" :rate 1820}
                      ;; {:date "2021-10-10T14:30:14" :rate 1811}
                      ;; {:date "2021-10-10T19:50:32" :rate 1831}
                      ;; {:date "2021-10-11T11:29:31" :rate 1747}
                      ;; {:date "2021-10-11T15:32:55" :rate 1736}
                      ;; {:date "2021-10-16T07:15:11" :rate 1624}
                      ;; {:date "2021-11-07T01:06:38" :rate 1608}
                      ;; {:date "2021-11-07T12:38:45" :rate 1608}
                      ;; {:date "2021-11-07T12:36:28" :rate 1616.98454696378}
                      ;; {:date "2021-11-07T13:05:16" :rate 1601.2499997998439}
                      ;; {:date "2021-11-07T12:10:16" :rate 1602.3992403345683}
                      ;; {:date "2021-11-07T12:15:16" :rate 1608.2339002518654}
                      ;; {:date "2021-11-07T16:00:00" :rate 1595}
                      ;; {:date "2021-11-10T18:07:37" :rate 1539}
                      ;; {:date "2022-07-09T09:50:00" :rate 4647}
                      ;; {:date "2022-08-20T09:13:00" :rate 4705}
                      ;; {:date "2022-09-03T18:50:00" :rate 5051}
                      ;; {:date "2022-09-05T11:06:00" :rate 5027}
                      {:date "2022-09-16T16:55:00" :rate 5066}
                      {:date "2022-12-03T13:43:16" :rate 5887}
                      {:date "2023-01-28T14:35:25" :rate 4340}
                      {:date "2023-03-11T12:08:00" :rate 4951}
                      {:date "2023-03-18T11:26:32" :rate 3644}
                      {:date "2023-03-22T12:00:48" :rate 3499}
                      {:date "2023-03-22T17:52:52" :rate 3680}
                      {:date "2023-03-23T11:16:28" :rate 3478}
                      {:date "2023-03-23T18:26:04" :rate 3554}
                      {:date "2023-03-30T19:04:27" :rate 3581}
                      {:date "2023-04-02T08:02:58" :rate 3528}
                      {:date "2023-04-04T19:50:17" :rate 3556}]]
           (ui-victory-chart
            {:domainPadding {:x 50}}
            (ui-victory-line
             {:data   value
              :style  (clj->js {:data {:stroke "#c43a31"}})
              :labels (fn [v] (comp/isoget-in v ["date" "rate"]))
              :x      "date"
              :y      "rate"})))))
     (u.links/ui-nav-menu {:menu-items menu-items :id id})
     ((comp/factory Router) router))))
