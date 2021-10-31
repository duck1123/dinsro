(ns dinsro.ui.admin-index-rate-sources-test
  (:require
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.admin-index-rate-sources :as u.admin-index-rate-sources]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as log]))

(ws/defcard AdminIndexRateSources
  {::wsm/align       {:flex 1}
   ::wsm/card-height 12
   ::wsm/card-width  6}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.admin-index-rate-sources/AdminIndexRateSources
    ::ct.fulcro3/initial-state
    (fn []
      (let [currency-id (new-uuid)
            currency    {::m.currencies/name "xccx"
                         ::m.currencies/id   currency-id}]
        {:component/id {}
         ::u.admin-index-rate-sources/rate-sources
         [{::m.rate-sources/id       (new-uuid)
           ::m.rate-sources/currency currency
           ::m.rate-sources/name     "cxcxd"
           ::m.rate-sources/url      "sdzxczx"}]}))}))
