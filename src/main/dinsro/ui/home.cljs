(ns dinsro.ui.home
  (:require
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.authorization :as auth]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.users :as m.users]
   [dinsro.ui.authenticator :as u.authenticator]
   [dinsro.ui.transactions :as u.transactions]))

(defn get-username
  [authenticator]
  (get-in authenticator
          [:local
           [::auth/authorization :local]
           :session/current-user
           ::m.users/name]))

(defsc Page
  [_this {:root/keys [authenticator]
          :ui/keys   [recent-transactions]}]
  {:componentDidMount #(report/start-report! % u.transactions/RecentReport
                                             {:route-params (comp/props %)})
   :css               [[:.container {:background-color "white"
                                     :margin-bottom    "30px"}]
                       [:.title {:color "blue" :font-weight "bold"}]]
   :ident             (fn [] [:component/id ::Page])
   :initial-state     {:component/id           ::Page
                       :ui/recent-transactions {}
                       :root/authenticator     {}}
   :query             [:component/id
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
         (dom/div :.ui.grid.center
           (dom/div :.two.column.row
             (dom/div :.column
               (dom/div :.ui.segment
                 ((comp/factory u.transactions/RecentReport)
                  recent-transactions)))
             (dom/div :.column
               (dom/div :.ui.segment "TODO: Put stuff here")))))))))
