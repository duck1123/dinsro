(ns dinsro.ui.admin-index-rate-sources-test
  (:require
   [dinsro.sample :as sample]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.admin-index-rate-sources :as u.admin-index-rate-sources]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

(ws/defcard AdminIndexRateSources
  {::wsm/align {:flex 1}
   ::wsm/card-height 15
   ::wsm/card-width 5}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.admin-index-rate-sources/AdminIndexRateSources
    ::ct.fulcro3/initial-state
    (fn [] {:rate-sources (map sample/rate-source-map [1 2])})
    ::ct.fulcro3/wrap-root? false}))
