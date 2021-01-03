(ns dinsro.views.index-rates
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.create-rate :as u.f.create-rate]
   [dinsro.ui.index-rates :as u.index-rates]
   [taoensso.timbre :as timbre]))

(defsc IndexRatesPage
  [_this {::keys [form rates toggle-button]}]
  {:ident (fn [] [:page/id ::page])
   :initial-state {::form          {}
                   ::rates         {}
                   ::toggle-button {}}
   :query [{::form          (comp/get-query u.f.create-rate/CreateRateForm)}
           {::rates         (comp/get-query u.index-rates/IndexRates)}
           {::toggle-button (comp/get-query u.buttons/ShowFormButton)}]
   :route-segment ["rates"]}
  (let [shown? false]
    (bulma/section
     (bulma/container
      (bulma/content
       (bulma/box
        (dom/h1
         (tr [:index-rates "Index Rates"])
         (u.buttons/ui-show-form-button toggle-button))
        (when shown?
          (u.f.create-rate/ui-create-rate-form form))
        (dom/hr)
        (u.index-rates/ui-index-rates rates)))))))

(def ui-page (comp/factory IndexRatesPage))
