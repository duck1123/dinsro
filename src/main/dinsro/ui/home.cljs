(ns dinsro.ui.home
  (:require
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.authorization :as auth]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid :refer [ui-grid]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid-column :refer [ui-grid-column]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid-row :refer [ui-grid-row]]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.users :as m.users]
   [dinsro.ui.authenticator :as u.authenticator]
   [dinsro.ui.home.accounts :as u.h.accounts]
   [dinsro.ui.home.core-nodes :as u.h.core-nodes]
   [dinsro.ui.home.ln-nodes :as u.h.ln-nodes]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.transactions :as u.transactions]
   [lambdaisland.glogc :as log]))

(def index-page-key :home)

(defn get-username
  [authenticator]
  (get-in authenticator
          [:local
           [::auth/authorization :local]
           :session/current-user
           ::m.users/name]))

(defsc Page
  [_this {:root/keys [authenticator]
          :ui/keys   [accounts ln-nodes nodes recent-transactions]
          :as        props}]
  {:componentDidMount (fn [this]
                        (report/start-report! this u.transactions/RecentReport {:route-params (comp/props this)})
                        (report/start-report! this u.h.accounts/Report {:route-params (comp/props this)})
                        (report/start-report! this u.h.core-nodes/Report {:route-params (comp/props this)})
                        (report/start-report! this u.h.ln-nodes/Report {:route-params (comp/props this)}))
   :css               [[:.container {:background-color "white"
                                     :margin-bottom    "30px"}]
                       [:.title {:color "blue" :font-weight "bold"}]]
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id         index-page-key
                       :ui/accounts            {}
                       :ui/ln-nodes            {}
                       :ui/nodes               {}
                       :ui/recent-transactions {}
                       :root/authenticator     {}}
   :query             [::m.navlinks/id
                       ::m.navlinks/label
                       {:ui/accounts (comp/get-query u.h.accounts/Report)}
                       {:ui/ln-nodes (comp/get-query u.h.ln-nodes/Report)}
                       {:ui/nodes (comp/get-query u.h.core-nodes/Report)}
                       {:ui/recent-transactions (comp/get-query u.transactions/RecentReport)}
                       {:root/authenticator (comp/get-query u.authenticator/UserAuthenticator)}]
   :route-segment     [""]
   :will-enter        (u.loader/page-loader index-page-key)}
  (log/info :Page/starting {:props props})
  (let [{:keys [container title]} (css/get-classnames Page)
        username                  (get-username authenticator)]
    (dom/div {}
      (dom/div {:classes [:.ui :.inverted :.vertical :.masthead
                          :.center :.aligned :.segment container]}
        (dom/div :.ui.container
          (ui-segment {}
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
                (ui-grid {:padded true}
                  (ui-grid-row {}
                    (ui-grid-column {:width 8}
                      (u.h.accounts/ui-report accounts))
                    (ui-grid-column {:width 8}
                      (u.h.core-nodes/ui-report nodes))
                    (ui-grid-column {:width 8}
                      (u.h.ln-nodes/ui-report ln-nodes))))))))))))
