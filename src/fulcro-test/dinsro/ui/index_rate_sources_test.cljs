(ns dinsro.ui.index-rate-sources-test
  (:require
   [dinsro.translations :refer [tr]]
   [dinsro.ui.index-rate-sources :as u.index-rate-sources]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

(ws/defcard IndexRateSourceLine
  {::wsm/card-height 5
   ::wsm/card-width 2}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.index-rate-sources/IndexRateSourceLine
    ::ct.fulcro3/initial-state
    (fn [] {:index-rate-source-line/name "bob"})
    ::ct.fulcro3/wrap-root? false}))

(ws/defcard IndexRateSources
  {::wsm/card-height 7
   ::wsm/card-width 2}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.index-rate-sources/IndexRateSources
    ::ct.fulcro3/initial-state
    (fn [] {:index-rate-sources/data
            [{:index-rate-source-line/name "Bobby"
              :index-rate-source-line/currency-id 1
              :index-rate-source-line/url "https://www.example.com/"}
             {:index-rate-source-line/name "Sally"
              :index-rate-source-line/currency-id 1
              :index-rate-source-line/url "https://www.example.com/1"}
             {:index-rate-source-line/name "George"
              :index-rate-source-line/currency-id nil
              :index-rate-source-line/url "https://www.example.com/2"}
             {:index-rate-source-line/name "Fred"
              :index-rate-source-line/currency-id 2
              :index-rate-source-line/url "https://www.example.com/3"}]})
    ::ct.fulcro3/wrap-root? false}))
