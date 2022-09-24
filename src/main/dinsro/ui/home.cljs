(ns dinsro.ui.home
  (:require
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.users :as m.users]
   [dinsro.ui.authenticator :as u.authenticator]
   [lambdaisland.glogc :as log]))

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
