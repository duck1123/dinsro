(ns dinsro.ui.admin-index-rate-sources
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [dinsro.machines :as machines]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.admin-create-rate-source :as u.f.admin-create-rate-source]
   [dinsro.ui.links :as u.links]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(def form-toggle-sm ::form-toggle)

(defsc AdminIndexRateSourceLine
  [_this {::m.rate-sources/keys [currency id url]
          :as                   rate-source}]
  {:ident         ::m.rate-sources/id
   :initial-state {::rate-source             {::m.rate-sources/id 1}
                   ::m.rate-sources/name     ""
                   ::m.rate-sources/currency {}
                   ::m.rate-sources/id       0
                   ::m.rate-sources/url      ""}
   :query         [{::rate-source (comp/get-query u.links/RateSourceLink)}
                   {::m.rate-sources/currency (comp/get-query u.links/CurrencyLink)}
                   ::m.rate-sources/id
                   ::m.rate-sources/name
                   ::m.rate-sources/url]}
  (dom/tr {}
    (dom/td (u.links/ui-rate-source-link rate-source))
    (dom/td url)
    (dom/td (u.links/ui-currency-link (first currency)))
    (dom/td (u.buttons/ui-delete-rate-source-button {::m.rate-sources/id id}))))

(def ui-admin-index-rate-source-line
  (comp/factory AdminIndexRateSourceLine {:keyfn ::m.rate-sources/id}))

(defsc AdminIndexRateSources
  [this {::keys [form rate-sources toggle-button]}]
  {:componentDidMount #(uism/begin! % machines/hideable form-toggle-sm {:actor/navbar AdminIndexRateSources})
   :ident             (fn [_] [:component/id ::AdminIndexRateSources])
   :initial-state     {::form          {}
                       ::rate-sources  []
                       ::toggle-button {:form-button/id form-toggle-sm}}
   :query             [:component/id
                       {::form (comp/get-query u.f.admin-create-rate-source/AdminCreateRateSourceForm)}
                       {::rate-sources (comp/get-query AdminIndexRateSourceLine)}
                       {::toggle-button (comp/get-query u.buttons/ShowFormButton)}
                       [::uism/asm-id form-toggle-sm]]}
  (let [shown? (= (uism/get-active-state this form-toggle-sm) :state/shown)]
    (bulma/box
     (dom/h2 :.title.is-2
       (tr [:rate-sources])
       (u.buttons/ui-show-form-button toggle-button))
     (when shown?
       (u.f.admin-create-rate-source/ui-admin-create-rate-source-form form))
     (dom/hr)
     (if (empty? rate-sources)
       (dom/p (tr [:no-rate-sources]))
       (dom/table :.table.is-fullwidth
         (dom/thead {}
           (dom/tr {}
             (dom/th "name")
             (dom/th "url")
             (dom/th "currency")
             (dom/th "actions")))
         (dom/tbody {}
           (map ui-admin-index-rate-source-line rate-sources)))))))

(def ui-section (comp/factory AdminIndexRateSources))
