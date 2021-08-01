(ns dinsro.views.show-user
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.model.users :as m.users]
   [dinsro.ui.show-user :as u.show-user]
   [dinsro.ui.user-accounts :as u.user-accounts]
   [taoensso.timbre :as log]))

(defsc UserAccounts
  [_this _props]
  {:ident         ::m.users/id
   :initial-state {::m.users/id       nil
                   ::m.users/accounts []}
   :query         [{::m.users/accounts (comp/get-query u.user-accounts/IndexAccountLine)}
                   ::m.users/id]})

(defsc ShowUserPage
  [_this {::m.users/keys [link]}]
  {:ident         (fn [] [:page/id ::page])
   :initial-state {::m.users/link {}}
   :query         [::m.users/id
                   {::m.users/link (comp/get-query u.show-user/ShowUserFull)}]
   :route-segment ["users" ::m.users/id]
   :will-enter
   (fn [app {::m.users/keys [id]}]
     (when id
       (df/load app [::m.users/id (new-uuid id)] u.show-user/ShowUserFull
                {:target [:page/id ::page ::m.users/link]}))
     (dr/route-immediate (comp/get-ident ShowUserPage {})))}
  (when link (u.show-user/ui-show-user-full link)))
