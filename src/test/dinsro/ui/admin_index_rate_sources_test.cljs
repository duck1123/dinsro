(ns dinsro.ui.admin-index-rate-sources-test
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.admin-index-rate-sources :as u.admin-index-rate-sources]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.admin-create-rate-source :as u.f.admin-create-rate-source]
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
      {::u.f.admin-create-rate-source/form
       (comp/get-initial-state u.f.admin-create-rate-source/AdminCreateRateSourceForm)

       :component/id {}
       ::u.admin-index-rate-sources/rate-sources
       [{::m.rate-sources/id       1
         ::m.rate-sources/currency [{::m.currencies/name "xccx"
                                     ::m.currencies/id   1}]
         ::m.rate-sources/name     "cxcxd"
         ::m.rate-sources/url      "sdzxczx"}]

       ::u.admin-index-rate-sources/toggle-button
       (comp/get-initial-state u.buttons/ShowFormButton)})}))
