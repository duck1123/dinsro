(ns dinsro.ui.admin-index-currencies
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.admin-create-currency :as u.f.admin-create-currency]
   [taoensso.timbre :as timbre]))

(defsc AdminIndexCurrencyLine
  [_this {::m.currencies/keys [id name]}]
  {:ident ::m.currencies/id
   :query [::m.currencies/id ::m.currencies/name]}
  (dom/tr
   (dom/td name)
   (dom/td id)))

(def ui-admin-index-currency-line (comp/factory AdminIndexCurrencyLine {:keyfn ::m.currencies/id}))

(defsc AdminIndexCurrencies
  [_this {::keys [currencies]}]
  {:initial-state {::currencies []}
   :query [{::currencies (comp/get-query AdminIndexCurrencyLine)}]}
  (if (seq currencies)
    (dom/table
     :.table
     (dom/thead
      (dom/tr
       (dom/th (tr [:name-label]))
       (dom/th "Buttons")))

     (dom/tbody
      (map ui-admin-index-currency-line currencies)))

    (dom/div (tr [:no-currencies]))))

(def ui-admin-index-currencies (comp/factory AdminIndexCurrencies))

(defsc AdminIndexCurrenciesSection
  [_this {::keys [currencies form toggle-button]}]
  {:initial-state {::currencies    {}
                   ::form          {}
                   ::toggle-button {}}
   :query [{::currencies  (comp/get-query AdminIndexCurrencies)}
           {::form   (comp/get-query u.f.admin-create-currency/AdminCreateCurrencyForm)}
           {::toggle-button (comp/get-query u.buttons/ShowFormButton)}]}
  (bulma/box
   (dom/h1
    (tr [:admin-index-currencies "Admin Index Currencies"])
    (u.buttons/ui-show-form-button toggle-button))
   (u.f.admin-create-currency/ui-admin-create-currency-form form)
   (dom/hr)
   (ui-admin-index-currencies currencies)))

(def ui-section (comp/factory AdminIndexCurrencies))
