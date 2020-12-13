(ns dinsro.views.index-rates
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.sample :as sample]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.create-rate :as u.f.create-rate]
   [dinsro.ui.index-rates :as u.index-rates]
   [taoensso.timbre :as timbre]))

(defsc IndexRatesPage
  [_this {:keys [button-data rates form-data]}]
  {:query [{:button-data (comp/get-query u.buttons/ShowFormButton)}
           {:form-data (comp/get-query u.f.create-rate/CreateRateForm)}
           {:rates (comp/get-query u.index-rates/IndexRates)}]
   :ident (fn [] [:page/id ::page])
   :initial-state {:button-data {}
                   :form-data {}
                   :rates {:rates/items (vals sample/rate-map)}}
   :route-segment ["rates"]}
  (dom/section
   :.section
   (dom/div
    :.container
    (dom/div
     :.content
     (dom/div
      :.box
      (dom/h1
       (tr [:index-rates "Index Rates"])
       (u.buttons/ui-show-form-button button-data))
      (u.f.create-rate/ui-create-rate-form form-data)
      (dom/hr)
      (u.index-rates/ui-index-rates rates))))))

(def ui-page (comp/factory IndexRatesPage))
