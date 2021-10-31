(ns dinsro.ui.admin-index-currencies
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]
   [taoensso.timbre :as log]))

(defsc AdminIndexCurrencyLine
  [_this {::m.currencies/keys [id link]}]
  {:ident         ::m.currencies/id
   :initial-state {::m.currencies/id   nil
                   ::m.currencies/link []}
   :query         [::m.currencies/id
                   {::m.currencies/link (comp/get-query u.links/CurrencyLink)}]}
  (dom/tr {}
    (dom/td (u.links/ui-currency-link link))
    (dom/td (u.buttons/ui-delete-currency-button {::m.currencies/id id}))))

(def ui-admin-index-currency-line (comp/factory AdminIndexCurrencyLine {:keyfn ::m.currencies/id}))

(defsc AdminIndexCurrencies
  [_this {::keys [currencies]}]
  {:ident             (fn [_] [:component/id ::AdminIndexCurrencies])
   :initial-state     {::currencies    []}
   :query             [{::currencies (comp/get-query AdminIndexCurrencyLine)}]}
  (bulma/box
   (dom/h2 :.title.is-2
     (tr [:currencies]))
   (dom/hr)
   (if (seq currencies)
     (dom/table :.table.ui
       (dom/thead {}
         (dom/tr {}
           (dom/th (tr [:name-label]))
           (dom/th "Buttons")))
       (dom/tbody {}
         (map ui-admin-index-currency-line currencies)))
     (dom/div {} (tr [:no-currencies])))))

(def ui-section (comp/factory AdminIndexCurrencies))
