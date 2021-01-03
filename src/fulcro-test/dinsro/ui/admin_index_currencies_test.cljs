(ns dinsro.ui.admin-index-currencies-test
  (:require
   [dinsro.sample :as sample]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.admin-index-currencies :as u.admin-index-currencies]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as timbre]))

(ws/defcard AdminIndexCurrenciesSection
  {::wsm/align       {:flex 1}
   ::wsm/card-height 10
   ::wsm/card-width  7}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.admin-index-currencies/AdminIndexCurrenciesSection
    ::ct.fulcro3/initial-state
    (fn [] {::u.admin-index-currencies/currencies
            {::u.admin-index-currencies/currencies (map sample/currency-map [1 2])}
            ::u.admin-index-currencies/form          {}
            ::u.admin-index-currencies/toggle-button {}})
    ::ct.fulcro3/wrap-root? false}))
