(ns dinsro.ui.index-currencies
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.user-currencies :as u.user-currencies]
   [taoensso.timbre :as log]))

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

(defsc IndexCurrenciesPage
  [_this {::keys [currencies]}]
  {:componentDidMount
   (fn [this]
     (df/load! this ::m.currencies/all-currencies IndexCurrencyLine
               {:target [:component/id
                         ::UserCurrencies
                         ::currencies
                         ::currencies]}))
   :ident         (fn [] [:page/id ::page])
   :initial-state {::currencies {}}
   :query         [{::currencies (comp/get-query u.user-currencies/UserCurrencies)}]
   :route-segment ["currencies"]}
  (bulma/page
   (u.user-currencies/ui-user-currencies currencies)))

(def ui-page (comp/factory IndexCurrenciesPage))
