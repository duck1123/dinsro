(ns dinsro.views.index-currencies
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.create-currency :as u.f.create-currency]
   [dinsro.ui.index-currencies :as u.index-currencies]
   [taoensso.timbre :as timbre]))

(defsc IndexCurrenciesPage
  [_this {::keys [currencies form toggle-button]}]
  {:ident (fn [] [:page/id ::page])
   :initial-state {::currencies    {}
                   ::form          {}
                   ::toggle-button {}}
   :query [{::currencies    (comp/get-query u.index-currencies/IndexCurrencies)}
           {::form          (comp/get-query u.f.create-currency/CreateCurrencyForm)}
           {::toggle-button (comp/get-query u.buttons/ShowFormButton)}]
   :route-segment ["currencies"]}
  (let [shown? false]
    (bulma/section
     (bulma/container
      (bulma/content
       (bulma/content
        (bulma/box
         (dom/h1
          (tr [:index-currencies "Index Currencies"])
          (u.buttons/ui-show-form-button toggle-button))
         (when shown?
           (u.f.create-currency/ui-create-currency-form form))
         (dom/hr)
         (u.index-currencies/ui-index-currencies currencies))))))))

(def ui-page (comp/factory IndexCurrenciesPage))
