(ns dinsro.ui.home
  (:require
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.authorization :as auth]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid :refer [ui-grid]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid-column :refer [ui-grid-column]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid-row :refer [ui-grid-row]]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.users :as m.users]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.authenticator :as u.authenticator]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.reports.home.accounts :as u.r.h.accounts]
   [dinsro.ui.reports.home.core-nodes :as u.r.h.core-nodes]
   [dinsro.ui.reports.home.ln-nodes :as u.r.h.ln-nodes]
   [dinsro.ui.reports.transactions :as u.r.transactions]
   [lambdaisland.glogc :as log]))

;; [[../mocks/ui/home.cljc]]

(def index-page-id :home)
(def parent-router-id :root)
(def required-role :guest)

(defn get-username
  [authenticator]
  (get-in authenticator
          [:local
           [::auth/authorization :local]
           :session/current-user
           ::m.users/name]))

(defsc IndexPage
  [_this {:root/keys [authenticator]
          :ui/keys   [accounts ln-nodes nodes recent-transactions]
          :as        props}]
  {:componentDidMount (fn [this]
                        (report/start-report! this u.r.transactions/RecentReport {:route-params (comp/props this)})
                        (report/start-report! this u.r.h.accounts/Report {:route-params (comp/props this)})
                        (report/start-report! this u.r.h.core-nodes/Report {:route-params (comp/props this)})
                        (report/start-report! this u.r.h.ln-nodes/Report {:route-params (comp/props this)}))
   :css               [[:.container {:background-color "white"
                                     :margin-bottom    "30px"}]
                       [:.title {:color "blue" :font-weight "bold"}]]
   :ident             (fn [] [::m.navlinks/id index-page-id])
   :initial-state     {::m.navlinks/id         index-page-id
                       :ui/accounts            {}
                       :ui/ln-nodes            {}
                       :ui/nodes               {}
                       :ui/recent-transactions {}
                       :root/authenticator     {}}
   :query             [::m.navlinks/id
                       ::m.navlinks/label
                       {:ui/accounts (comp/get-query u.r.h.accounts/Report)}
                       {:ui/ln-nodes (comp/get-query u.r.h.ln-nodes/Report)}
                       {:ui/nodes (comp/get-query u.r.h.core-nodes/Report)}
                       {:ui/recent-transactions (comp/get-query u.r.transactions/RecentReport)}
                       {:root/authenticator (comp/get-query u.authenticator/UserAuthenticator)}]
   :route-segment     [""]
   :will-enter        (u.loader/page-loader index-page-id)}
  (log/info :IndexPage/starting {:props props})
  (let [{:keys [container title]} (css/get-classnames IndexPage)
        username                  (get-username authenticator)]
    (dom/div {}
      (dom/div {:classes [:.ui :.inverted :.vertical :.masthead
                          :.center :.aligned :.segment container]}
        (ui-segment {}
          (dom/h1 {:classes [:.ui :.header title]}
            (if username
              (str "Welcome, " username)
              "Home Page")))
        (when username
          (ui-grid {:centered true :padded true}
            (ui-grid-row {}
              (ui-grid-column {:tablet 8 :mobile 16 :computer 8}
                ((comp/factory u.r.transactions/RecentReport) recent-transactions))
              (ui-grid-column {:tablet 8 :mobile 16 :computer 8}
                (ui-grid {}
                  (ui-grid-row {}
                    (ui-grid-column {:computer 8 :tablet 16}
                      (u.r.h.accounts/ui-report accounts))
                    (ui-grid-column {:computer 8 :tablet 16}
                      (ui-grid {}
                        (ui-grid-row {}
                          (ui-grid-column {:width 16}
                            (u.r.h.core-nodes/ui-report nodes)))
                        (ui-grid-row {}
                          (ui-grid-column {:width 16}
                            (u.r.h.ln-nodes/ui-report ln-nodes)))))))))))))))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::IndexPage
   o.navlinks/label         "Home"
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
