(ns dinsro.ui.currency-rate-sources
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [dinsro.machines :as machines]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.add-currency-rate-source :as u.f.add-currency-rate-source]
   [dinsro.ui.links :as u.links]
   [taoensso.timbre :as timbre]))

(def form-toggle-sm ::form-toggle)

(def default-name "sally")
(def default-url "https://example.com/")

(defsc IndexRateSourceLine
  [_this {::m.rate-sources/keys [currency id url] :as rate-source}]
  {:ident ::m.rate-sources/id
   :initial-state {::rate-source             {}
                   ::m.rate-sources/currency {}
                   ::m.rate-sources/id       0
                   ::m.rate-sources/name     ""
                   ::m.rate-sources/url      ""}
   :query [{::rate-source             (comp/get-query u.links/RateSourceLink)}
           ::m.rate-sources/id
           {::m.rate-sources/currency (comp/get-query u.links/CurrencyLink)}
           ::m.rate-sources/name
           ::m.rate-sources/url]}
  (dom/tr
   (dom/td (u.links/ui-rate-source-link rate-source))
   (dom/td url)
   (dom/td (u.links/ui-currency-link currency))
   (dom/td
    (u.buttons/ui-delete-button {::m.rate-sources/id id}))))

(def ui-index-rate-source-line (comp/factory IndexRateSourceLine {:keyfn ::m.rate-sources/id}))

(defsc IndexCurrencyRateSources
  [_this {::keys [items]}]
  {:initial-state {::items []}
   :query [{::items (comp/get-query IndexRateSourceLine)}]}
  (if (seq items)
    (dom/table
     :.table
     (dom/thead
      (dom/tr
       (dom/th (tr [:name]))
       (dom/th (tr [:url]))
       (dom/th (tr [:currency]))
       (dom/th (tr [:actions]))))
     (dom/tbody
      (map ui-index-rate-source-line items)))
    (dom/p "no items")))

(def ui-index-currency-rate-sources (comp/factory IndexCurrencyRateSources))

(defsc CurrencyRateSources
  [this {::keys [form rate-sources toggle-button]}]
  {:componentDidMount
   #(uism/begin! % machines/hideable form-toggle-sm {:actor/navbar CurrencyRateSources})
   :ident (fn [_] [:component/id ::CurrencyRateSources])
   :initial-state {::rate-sources  {}
                   ::form          {}
                   ::toggle-button {:form-button/id form-toggle-sm}}
   :query [{::rate-sources  (comp/get-query IndexCurrencyRateSources)}
           {::form          (comp/get-query u.f.add-currency-rate-source/AddCurrencyRateSourceForm)}
           {::toggle-button (comp/get-query u.buttons/ShowFormButton)}
           [::uism/asm-id form-toggle-sm]]}
  (let [shown? (= (uism/get-active-state this form-toggle-sm) :state/shown)]
    (bulma/box
     (dom/h2
      (tr [:rate-sources])
      (u.buttons/ui-show-form-button toggle-button))
     (when shown?
       (u.f.add-currency-rate-source/ui-form form))
     (dom/hr)
     (ui-index-currency-rate-sources rate-sources))))

(def ui-currency-rate-sources (comp/factory CurrencyRateSources))
