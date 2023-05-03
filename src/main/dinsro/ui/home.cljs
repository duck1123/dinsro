(ns dinsro.ui.home
  (:require
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.authorization :as auth]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid :refer [ui-grid]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid-column :refer [ui-grid-column]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid-row :refer [ui-grid-row]]
   [com.fulcrologic.semantic-ui.elements.container.ui-container :refer [ui-container]]
   [dinsro.joins.core.nodes :as j.c.nodes]
   [dinsro.joins.ln.nodes :as j.ln.nodes]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.ln.info :as m.ln.info]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.users :as m.users]
   [dinsro.ui.authenticator :as u.authenticator]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.transactions :as u.transactions]))

(defn get-username
  [authenticator]
  (get-in authenticator
          [:local
           [::auth/authorization :local]
           :session/current-user
           ::m.users/name]))

(report/defsc-report LnNodeReport
  [_this _props]
  {ro/column-formatters {::m.ln.nodes/name      #(u.links/ui-node-link %3)
                         ::m.ln.nodes/network   #(u.links/ui-network-link %2)
                         ::m.ln.nodes/user      #(u.links/ui-user-link %2)
                         ::m.ln.nodes/core-node #(u.links/ui-core-node-link %2)}
   ro/columns           [m.ln.nodes/name
                         m.ln.nodes/network
                         m.ln.info/alias-attr
                         m.ln.info/color]
   ro/control-layout    {:action-buttons [::new-node ::refresh]}
   ro/controls          {::refresh  u.links/refresh-control}
   ro/row-pk            m.ln.nodes/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.ln.nodes/index
   ro/title             "Lightning Nodes"})

(def ui-ln-node-report (comp/factory LnNodeReport))

(report/defsc-report NodeReport
  [_this _props]
  {ro/column-formatters {::m.c.nodes/name    #(u.links/ui-core-node-link %3)
                         ::m.c.nodes/network #(u.links/ui-network-link %2)}
   ro/columns           [m.c.nodes/name
                         m.c.nodes/host
                         m.c.nodes/network]
   ro/control-layout    {:action-buttons [::new ::refresh]}
   ro/controls          {::refresh u.links/refresh-control}
   ro/row-pk            m.c.nodes/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.nodes/index
   ro/title             "Core Nodes"})

(def ui-node-report (comp/factory NodeReport))

(defsc Page
  [_this {:root/keys [authenticator]
          :ui/keys   [ln-nodes nodes recent-transactions]}]
  {:componentDidMount (fn [this]
                        (report/start-report! this u.transactions/RecentReport {:route-params (comp/props this)})
                        (report/start-report! this NodeReport {:route-params (comp/props this)})
                        (report/start-report! this LnNodeReport {:route-params (comp/props this)}))
   :css               [[:.container {:background-color "white"
                                     :margin-bottom    "30px"}]
                       [:.title {:color "blue" :font-weight "bold"}]]
   :ident             (fn [] [:component/id ::Page])
   :initial-state     {:component/id           ::Page
                       :ui/ln-nodes            {}
                       :ui/nodes               {}
                       :ui/recent-transactions {}
                       :root/authenticator     {}}
   :query             [:component/id
                       {:ui/ln-nodes (comp/get-query LnNodeReport)}
                       {:ui/nodes (comp/get-query NodeReport)}
                       {:ui/recent-transactions (comp/get-query u.transactions/RecentReport)}
                       {:root/authenticator (comp/get-query u.authenticator/UserAuthenticator)}]
   :route-segment     [""]}
  (let [{:keys [container title]} (css/get-classnames Page)
        username                  (get-username authenticator)]
    (comp/fragment
     (dom/div {:classes [:.ui :.inverted :.vertical :.masthead
                         :.center :.aligned :.segment container]}
       (dom/div :.ui.container
         (dom/div :.ui.segment
           (dom/h1 {:classes [:.ui :.header title]}
             (if username
               (str "Welcome, " username)
               "Home Page"))))
       (when username
         (ui-grid {:centered true :padded true}
           (ui-grid-row {}
             (ui-grid-column {:tablet 8 :mobile 16 :computer 8}
               ((comp/factory u.transactions/RecentReport) recent-transactions))
             (ui-grid-column {:tablet 8 :mobile 16 :computer 8}
               (ui-container {}
                 (ui-node-report nodes)
                 (ui-ln-node-report ln-nodes))))))))))
