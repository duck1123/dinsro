(ns dinsro.ui.admin-index-rate-sources-test
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [dinsro.sample :as sample]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.admin-index-rate-sources :as u.admin-index-rate-sources]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.admin-create-rate-source :as u.f.admin-create-rate-source]
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
      {::u.f.admin-create-rate-source/form
       (comp/get-initial-state u.f.admin-create-rate-source/AdminCreateRateSourceForm)

       ::u.f.admin-create-rate-source/rate-sources
       (map (fn [m] (assoc m ::u.admin-index-rate-sources/button-data {}))
            (vals sample/rate-source-map))

       ::u.f.admin-create-rate-source/toggle-button
       (comp/get-initial-state u.buttons/ShowFormButton)})
    ::ct.fulcro3/wrap-root? false}))
