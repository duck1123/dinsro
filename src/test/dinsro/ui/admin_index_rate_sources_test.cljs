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
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as timbre]))

(ws/defcard AdminIndexRateSources
  {::wsm/align       {:flex 1}
   ::wsm/card-height 12
   ::wsm/card-width  6}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.admin-index-rate-sources/AdminIndexRateSources
    ;; ::ct.fulcro3/wrap-root? false
    ::ct.fulcro3/initial-state
    (fn []
      {:component/id {}
       ::u.admin-index-rate-sources/form
       (comp/get-initial-state u.f.admin-create-rate-source/AdminCreateRateSourceForm)

       ::u.admin-index-rate-sources/rate-sources
       (map (fn [m] (assoc m ::u.admin-index-rate-sources/button-data {}))
            (vals sample/rate-source-map))

       ::u.admin-index-rate-sources/toggle-button
       (comp/get-initial-state u.buttons/ShowFormButton)})}))
