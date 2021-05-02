(ns dinsro.ui.admin-index-currencies
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [dinsro.machines :as machines]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.admin-create-currency :as u.f.admin-create-currency]
   [dinsro.ui.links :as u.links]
   [taoensso.timbre :as timbre]))

(def form-toggle-sm ::form-toggle)

(defsc AdminIndexCurrencyLine
  [_this {::m.currencies/keys [id link]}]
  {:ident         ::m.currencies/id
   :initial-state {::m.currencies/id   ""
                   ::m.currencies/link []}
   :query         [::m.currencies/id
                   {::m.currencies/link (comp/get-query u.links/CurrencyLink)}]}
  (dom/tr
   (dom/td (u.links/ui-currency-link (first link)))
   (dom/td (u.buttons/ui-delete-currency-button {::m.currencies/id id}))))

(def ui-admin-index-currency-line (comp/factory AdminIndexCurrencyLine {:keyfn ::m.currencies/id}))

(defsc AdminIndexCurrencies
  [this {::keys [currencies form toggle-button]}]
  {:componentDidMount #(uism/begin! % machines/hideable form-toggle-sm {:actor/navbar AdminIndexCurrencies})
   :ident             (fn [_] [:component/id ::AdminIndexCurrencies])
   :initial-state     {::currencies    []
                       ::form          {}
                       ::toggle-button {:form-button/id form-toggle-sm}}
   :query             [{::currencies (comp/get-query AdminIndexCurrencyLine)}
                       {::form (comp/get-query u.f.admin-create-currency/AdminCreateCurrencyForm)}
                       {::toggle-button (comp/get-query u.buttons/ShowFormButton)}
                       [::uism/asm-id form-toggle-sm]]}
  (let [shown? (= (uism/get-active-state this form-toggle-sm) :state/shown)]
    (bulma/box
     (dom/h2
      :.title.is-2
      (tr [:currencies])
      (u.buttons/ui-show-form-button toggle-button))
     (when shown?
       (u.f.admin-create-currency/ui-admin-create-currency-form form))
     (dom/hr)
     (if (seq currencies)
       (dom/table
        :.table
        (dom/thead
         (dom/tr
          (dom/th (tr [:name-label]))
          (dom/th "Buttons")))
        (dom/tbody
         (map ui-admin-index-currency-line currencies)))
       (dom/div (tr [:no-currencies]))))))

(def ui-section (comp/factory AdminIndexCurrencies))
