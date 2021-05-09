(ns dinsro.views.index-rate-sources-test
  (:require
   [dinsro.sample :as sample]
   [dinsro.ui.index-rate-sources :as u.index-rate-sources]
   [dinsro.views.index-rate-sources :as v.index-rate-sources]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as log]))

(ws/defcard IndexRateSourcesPage
  {::wsm/align       {:flex 1}
   ::wsm/card-height 11
   ::wsm/card-width  5}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root v.index-rate-sources/IndexRateSourcesPage
    ::ct.fulcro3/initial-state
    (fn []
      {::v.index-rate-sources/form          {}
       ::v.index-rate-sources/rate-sources
       {::u.index-rate-sources/items (vals sample/rate-source-map)}
       ::v.index-rate-sources/toggle-button {}})}))
