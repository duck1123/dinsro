(ns dinsro.ui.index-rate-sources-test
  (:require
   [dinsro.sample :as sample]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.index-rate-sources :as u.index-rate-sources]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

(comment
  (ws/defcard IndexRateSourceLine
    {::wsm/card-height 5
     ::wsm/card-width  2}
    (ct.fulcro3/fulcro-card
     {::ct.fulcro3/root u.index-rate-sources/IndexRateSourceLine
      ::ct.fulcro3/initial-state
      (fn [] {:index-rate-source-line/name "bob"})
      ::ct.fulcro3/wrap-root? false})))

(ws/defcard IndexRateSources
  {::wsm/card-height 3
   ::wsm/card-width  2}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.index-rate-sources/IndexRateSources
    ::ct.fulcro3/initial-state
    (fn [] {:index-rate-source-line/id sample/rate-source-map
            :index-rate-sources/items (map sample/rate-source-map [1 2 3 4])})
    ::ct.fulcro3/wrap-root? false}))
