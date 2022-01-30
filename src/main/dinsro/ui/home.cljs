(ns dinsro.ui.home
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.rad.control :as control]
   [com.fulcrologic.rad.control-options :as copt]
   [com.fulcrologic.rad.container :as container :refer [defsc-container]]
   [com.fulcrologic.rad.container-options :as co]
   [dinsro.model.users :as m.users]
   [dinsro.ui.accounts :as u.accounts]
   [dinsro.ui.authenticator :as u.authenticator]
   [dinsro.ui.categories :as u.categories]
   [dinsro.ui.ln-nodes :as u.ln-nodes]
   [lambdaisland.glogc :as log]))

(defsc-container HomePage2
  [_this _props]
  {:ident         (fn [] [:component/id ::HomePage])
   :initial-state {:component/id ::HomePage}
   :query         [:component/id]
   :route-segment [""]
   co/children    {:categories u.categories/CategoriesSubReport
                   :accounts   u.accounts/AccountsSubReport
                   :ln-nodes   u.ln-nodes/LNNodesSubReport}
   co/route       ""
   co/title       "Home Page"
   co/layout      [[{:id :categories :width 8} {:id :accounts :width 8}]
                   [{:id :ln-nodes :width 8}]]

   copt/controls       {::refresh {:type   :button
                                   :label  "Refresh"
                                   :action (fn [container] (control/run! container))}}
   copt/control-layout {:action-buttons [::refresh]}})

(defsc HomePage
  [_this {:root/keys [authenticator]}]
  {:route-segment [""]
   :query         [:component/id
                   {:root/authenticator (comp/get-query u.authenticator/UserAuthenticator)}]
   :initial-state {:component/id       ::HomePage
                   :root/authenticator {}}
   :css           [[:.container {:background-color "white"}]
                   [:.title {:color       "blue"
                             :font-weight "bold"}]]
   :ident         (fn [] [:component/id ::HomePage])}
  (log/info :home-page/rendered {:authenticator authenticator})
  (let [{:keys [container title]} (css/get-classnames HomePage)
        username                  (get-in authenticator [:local [:com.fulcrologic.rad.authorization/authorization :local] :session/current-user ::m.users/name])]
    (comp/fragment
     (dom/div {:classes [:.ui.inverted.vertical.masthead.center.aligned.segment container]}
       (dom/div :.ui.container
         (dom/div :.ui.text.container
           (dom/h1 {:classes [:.ui.inverted.header title]}
                   (if username
                     (str "Welcome, " username)
                     "Home Page"))
           (dom/h2 {} "TODO: put stuff here")))))))
