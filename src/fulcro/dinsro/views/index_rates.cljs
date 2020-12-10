(ns dinsro.views.index-rates
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.create-rate :as u.f.create-rate]
   [dinsro.ui.index-rates :as u.index-rates]
   [taoensso.timbre :as timbre]))

(defsc IndexRatesPage
  [_this {::keys [show-form-button rates form]}]
  {:query [{::show-form-button (comp/get-query u.buttons/ShowFormButton)}
           {::form (comp/get-query u.f.create-rate/CreateRateForm)}
           {::rates (comp/get-query u.index-rates/IndexRates)}]
   :ident (fn [] [:page/id ::page])
   :initial-state
   (fn [_]
     {::show-form-button (comp/get-initial-state u.buttons/ShowFormButton)
      ::form (comp/get-initial-state u.f.create-rate/CreateRateForm)
      ::rates (comp/get-initial-state u.index-rates/IndexRates)})
   :route-segment ["rates"]}
  (dom/section
   :.section
   (dom/div
    :.container
    (dom/div
     :.content
     (dom/div
      :.container
      (dom/div
       :.content
       (dom/div
        :.box
        (dom/h1
         (tr [:index-rates "Index Rates"])
         (u.buttons/ui-show-form-button show-form-button))
        (u.f.create-rate/ui-create-rate-form form)
        (dom/hr)
        (u.index-rates/ui-index-rates rates))))))))

(def ui-page (comp/factory IndexRatesPage))
