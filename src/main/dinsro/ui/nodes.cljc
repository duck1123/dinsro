(ns dinsro.ui.nodes
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.nodes.core :as u.no.core]
   [dinsro.ui.nodes.ln :as u.no.ln]))

(def index-page-key :nodes)

(defsc Dashboard
  [_this {:ui/keys [core-report ln-report]}]
  {:componentDidMount (fn [this]
                        (report/start-report! this u.no.core/Report {:route-params (comp/props this)})
                        (report/start-report! this u.no.ln/Report {:route-params (comp/props this)}))
   :ident             (fn [] [:component/id ::Dashboard])
   :initial-state     {:ui/core-report {}
                       :ui/ln-report   {}}
   :query             [{:ui/core-report (comp/get-query u.no.core/Report)}
                       {:ui/ln-report (comp/get-query u.no.ln/Report)}]
   :route-segment     ["dashboard"]}
  (dom/div :.ui.grid
    (dom/div :.two.column.row
      (dom/div :.ui.column
        (dom/div :.ui.container
          (dom/div :.ui.segment
            (u.no.core/ui-report core-report))))
      (dom/div :.ui.column
        (dom/div :.ui.container
          (dom/div :.ui.segment
            (u.no.ln/ui-report ln-report)))))))

(defrouter Router
  [_this {:keys [route-factory route-props]}]
  {:router-targets [Dashboard]}
  (dom/div :.router
    (route-factory route-props)))

(def ui-router (comp/factory Router))

(defsc Page
  [_this {:ui/keys [router]}]
  {:ident         (fn [] [::m.navlinks/id index-page-key])
   :initial-state {::m.navlinks/id index-page-key
                   :ui/router      {}}
   :query         [::m.navlinks/id
                   {:ui/router (comp/get-query Router)}]
   :route-segment ["nodes"]}
  (ui-router router))

(m.navlinks/defroute   :nodes
  {::m.navlinks/control       ::Page
   ::m.navlinks/label         "Nodes"
   ::m.navlinks/parent-key    :root
   ::m.navlinks/required-role :user
   ::m.navlinks/router        :root})
