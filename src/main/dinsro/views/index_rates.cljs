(ns dinsro.views.index-rates
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [dinsro.machines :as machines]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.create-rate :as u.f.create-rate]
   [dinsro.ui.index-rates :as u.index-rates]
   [taoensso.timbre :as log]))

(def form-toggle-sm ::form-toggle)

(defsc IndexRatesPage
  [this {::keys [show-form-button rates form]}]
  {:componentDidMount #(uism/begin! % machines/hideable form-toggle-sm {:actor/navbar IndexRatesPage})
   :ident             (fn [] [:page/id ::page])
   :initial-state     {::form             {}
                       ::rates            {}
                       ::show-form-button {:form-button/id form-toggle-sm}}
   :query             [{::form (comp/get-query u.f.create-rate/CreateRateForm)}
                       {::rates (comp/get-query u.index-rates/IndexRates)}
                       {::show-form-button (comp/get-query u.buttons/ShowFormButton)}
                       [::uism/asm-id form-toggle-sm]]
   :route-segment     ["rates"]
   :will-enter
   (fn [app _props]
     (df/load! app :all-rates u.index-rates/IndexRateLine
               {:target [:page/id
                         ::page
                         ::rates
                         ::u.index-rates/rates]})
     (dr/route-immediate (comp/get-ident IndexRatesPage {})))}
  (let [shown? (= (uism/get-active-state this form-toggle-sm) :state/shown)]
    (bulma/page
     (bulma/box
      (dom/h1
       (tr [:index-rates "Index Rates"])
       (u.buttons/ui-show-form-button show-form-button))
      (when shown? (u.f.create-rate/ui-create-rate-form form))
      (dom/hr)
      (u.index-rates/ui-index-rates rates)))))

(def ui-page (comp/factory IndexRatesPage))
