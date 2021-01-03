(ns dinsro.ui.admin-index-rate-sources-test
  (:require
   [dinsro.sample :as sample]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.admin-index-rate-sources :as u.admin-index-rate-sources]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

(ws/defcard AdminIndexRateSources
  {::wsm/align       {:flex 1}
   ::wsm/card-height 12
   ::wsm/card-width  6}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.admin-index-rate-sources/AdminIndexRateSources
    ::ct.fulcro3/initial-state
    (fn []
      {::u.admin-index-rate-sources/form          {}
       ::u.admin-index-rate-sources/rate-sources
       (map (fn [m] (assoc m ::u.admin-index-rate-sources/button-data {}))
            (vals sample/rate-source-map))
       ::u.admin-index-rate-sources/toggle-button {}})
    ::ct.fulcro3/wrap-root? false}))
