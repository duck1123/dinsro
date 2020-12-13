(ns dinsro.views.index-rate-sources
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.create-rate-source :as u.f.create-rate-source]
   [dinsro.ui.index-rate-sources :as u.index-rate-sources]
   [taoensso.timbre :as timbre]))

(defsc IndexRateSourcesPage
  [_this {:keys [button-data rates form-data]}]
  {:query [{:button-data (comp/get-query u.buttons/ShowFormButton)}
           {:form-data (comp/get-query u.f.create-rate-source/CreateRateSourceForm)}
           {:rates (comp/get-query u.index-rate-sources/IndexRateSources)}]
   :ident (fn [] [:page/id ::page])
   :initial-state {:button-data {}
                   :form-data {}
                   :rates {}}
   :route-segment ["rate-sources"]}
  (dom/section
   :.section
   (dom/div
    :.container
    (dom/div
     :.content
     (dom/div
      :.box
      (dom/h1
       (tr [:index-rates "Index Rate Sources"])
       (u.buttons/ui-show-form-button button-data))
      (u.f.create-rate-source/ui-create-rate-source-form form-data)
      (dom/hr)
      (u.index-rate-sources/ui-index-rate-sources (timbre/spy :info rates)))))))

(def ui-page (comp/factory IndexRateSourcesPage))
