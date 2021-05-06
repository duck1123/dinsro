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
     {::ct.fulcro3/root       u.index-rate-sources/IndexRateSourceLine
      ::ct.fulcro3/initial-state
      (fn [] {:index-rate-source-line/name "bob"})
      ::ct.fulcro3/wrap-root? false})))

(def rate-sources (map sample/rate-source-map [1 2 3 4]))

(ws/defcard IndexRateSources
  {::wsm/card-height 8
   ::wsm/card-width  6}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root       u.index-rate-sources/IndexRateSources
    ::ct.fulcro3/initial-state
    (fn [] {::u.index-rate-sources/items rate-sources})}))
