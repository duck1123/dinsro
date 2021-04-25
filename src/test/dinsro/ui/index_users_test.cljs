(ns dinsro.ui.index-users-test
  (:require
   [dinsro.specs :as ds]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.index-users :as u.index-users]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

(defn get-state
  []
  (ds/gen-key ::u.index-users/IndexUsers-state))

(ws/defcard IndexUsers
  {::wsm/align       {:flex 1}
   ::wsm/card-height 7
   ::wsm/card-width  4}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root          u.index-users/IndexUsers
    ::ct.fulcro3/initial-state get-state}))
