(ns dinsro.ui.admin.users.categories-test
  (:require
   [dinsro.client :as client]
   [dinsro.joins.categories :as j.categories]
   [dinsro.model.categories :as m.categories]
   [dinsro.specs :as ds]
   [dinsro.ui.admin.users.categories :as u.a.u.categories]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]))

(defn make-body-item
  []
  {::m.categories/name              (ds/gen-key ::m.categories/name)
   ::m.categories/id                (ds/gen-key ::m.categories/id)
   ::j.categories/transaction-count (ds/gen-key ::j.categories/transaction-count)})

(ws/defcard BodyItem
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root          u.a.u.categories/BodyItem
    ::ct.fulcro3/app           {:client-will-mount client/setup-RAD}
    ::ct.fulcro3/initial-state (fn [] (make-body-item))}))
