(ns dinsro.ui.show-account-test
  (:require
   [dinsro.sample :as sample]
   [dinsro.translations :refer [tr]]

   [dinsro.ui.show-account :as u.show-account]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as timbre]))

(ws/defcard ShowAccount
  {::wsm/card-height 8
   ::wsm/card-width  3}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.show-account/ShowAccount
    ::ct.fulcro3/initial-state
    (fn [] (assoc (rand-nth (vals sample/account-map))
                  :user-link-data (rand-nth (vals sample/user-map))))}))
