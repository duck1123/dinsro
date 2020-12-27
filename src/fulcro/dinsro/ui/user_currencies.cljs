(ns dinsro.ui.user-currencies
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [dinsro.machines :as machines]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.create-currency :as u.f.create-currency]
   [dinsro.ui.index-currencies :as u.index-currencies]
   [taoensso.timbre :as timbre]))

(def form-toggle-sm ::form-toggle)

(defsc UserCurrencies
  [this {::keys [ currencies form toggle-button ]}]
  {:componentDidMount #(uism/begin! % machines/hideable form-toggle-sm {:actor/navbar UserCurrencies})
   :ident (fn [_] [:component/id ::UserCurrencies])
   :initial-state {::currencies    {}
                   ::form          {}
                   ::toggle-button {:form-button/id form-toggle-sm}}
   :query [{::currencies    (comp/get-query u.index-currencies/IndexCurrencies)}
           {::form          (comp/get-query u.f.create-currency/CreateCurrencyForm)}
           {::toggle-button (comp/get-query u.buttons/ShowFormButton)}
           [::uism/asm-id form-toggle-sm]]}
  (let [shown? (= (uism/get-active-state this form-toggle-sm) :state/shown)]
    (bulma/box
     (dom/h1
      (tr [:currencies])
      (u.buttons/ui-show-form-button toggle-button))
     (when shown? (u.f.create-currency/ui-create-currency-form form))
     (dom/hr)
     (u.index-currencies/ui-index-currencies currencies))))

(def ui-user-currencies (comp/factory UserCurrencies))
