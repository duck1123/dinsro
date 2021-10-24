(ns dinsro.ui.user-currencies
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [dinsro.machines :as machines]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.create-currency :as u.f.create-currency]
   [dinsro.ui.links :as u.links]
   [taoensso.timbre :as log]))

(def form-toggle-sm ::form-toggle)

(defsc IndexCurrencyLine
  [_this {::m.currencies/keys [code id link]}]
  {:ident         ::m.currencies/id
   :initial-state {::m.currencies/code ""
                   ::m.currencies/id   nil
                   ::m.currencies/link []}
   :query         [::m.currencies/code
                   ::m.currencies/id
                   {::m.currencies/link (comp/get-query u.links/ui-currency-link)}]}
  (dom/tr {}
    (dom/td code)
    (dom/td (u.links/ui-currency-link link))
    (dom/td (u.buttons/ui-delete-currency-button {::m.currencies/id id}))))

(def ui-index-currency-line (comp/factory IndexCurrencyLine {:keyfn ::m.currencies/id}))

(defsc IndexCurrencies
  [_this {::keys [currencies]}]
  {:initial-state {::currencies []}
   :query         [{::currencies (comp/get-query IndexCurrencyLine)}]}
  (if (seq currencies)
    (dom/table :.ui.table
      (dom/thead {}
        (dom/tr {}
          (dom/th {} (tr [:code]))
          (dom/th {} (tr [:name-label]))
          (dom/th {} "Buttons")))
      (dom/tbody {}
        (map ui-index-currency-line currencies)))
    (dom/div (tr [:no-currencies]))))

(def ui-index-currencies (comp/factory IndexCurrencies))

(defsc UserCurrencies
  [this {::keys [currencies form toggle-button]}]
  {:componentDidMount #(uism/begin! % machines/hideable form-toggle-sm {:actor/navbar UserCurrencies})
   :ident             (fn [_] [:component/id ::UserCurrencies])
   :initial-state     {::currencies    {}
                       ::form          {}
                       ::toggle-button {:form-button/id form-toggle-sm}}
   :query             [{::currencies (comp/get-query IndexCurrencies)}
                       {::form (comp/get-query u.f.create-currency/CreateCurrencyForm)}
                       {::toggle-button (comp/get-query u.buttons/ShowFormButton)}
                       [::uism/asm-id form-toggle-sm]]}
  (let [shown? (= (uism/get-active-state this form-toggle-sm) :state/shown)]
    (bulma/box
     (dom/h1
      (tr [:currencies])
      (u.buttons/ui-show-form-button toggle-button))
     (when shown? (u.f.create-currency/ui-create-currency-form form))
     (dom/hr)
     (ui-index-currencies currencies))))

(def ui-user-currencies (comp/factory UserCurrencies))
