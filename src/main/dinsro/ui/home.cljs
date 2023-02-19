(ns dinsro.ui.home
  (:require
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.authorization :as auth]
   [dinsro.model.users :as m.users]
   [dinsro.ui.authenticator :as u.authenticator]
   [dinsro.ui.transactions :as u.transactions]
   [lambdaisland.glogc :as log]))

(defn get-username
  [authenticator]
  (get-in authenticator
          [:local
           [::auth/authorization :local]
           :session/current-user
           ::m.users/name]))

(defsc HomePage
  [_this {:root/keys [authenticator]
          :ui/keys   [recent-transactions]}]
  {:route-segment [""]
   :query         [:component/id
                   :ui/recent-transactions
                   {:root/authenticator (comp/get-query u.authenticator/UserAuthenticator)}]
   :initial-state {:component/id           ::HomePage
                   :ui/recent-transactions (comp/get-query u.transactions/Report)
                   :root/authenticator     {}}
   :css           [[:.container {:background-color "white"}]
                   [:.title {:color       "blue"
                             :font-weight "bold"}]]
   :ident         (fn [] [:component/id ::HomePage])}
  (log/info :home-page/rendered {:authenticator authenticator})
  (let [{:keys [container title]} (css/get-classnames HomePage)
        username                  (get-username authenticator)]
    (comp/fragment
     (dom/div {:classes [:.ui :.inverted :.vertical :.masthead
                         :.center :.aligned :.segment container]}
       (dom/div :.ui.container
         (dom/div :.ui.text.container
           (dom/h1 {:classes [:.ui :.inverted :.header title]}
                   (if username
                     (str "Welcome, " username)
                     "Home Page"))
           (dom/h2 {} "TODO: put stuff here")
           (dom/div :.ui.segment
             (dom/h3 "Recent Transactions")
             (str recent-transactions))))))))
