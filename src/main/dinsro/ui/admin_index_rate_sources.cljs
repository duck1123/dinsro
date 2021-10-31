(ns dinsro.ui.admin-index-rate-sources
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as log]))

(defsc AdminIndexRateSourceLine
  [_this {::m.rate-sources/keys [currency id url]
          :as                   rate-source}]
  {:ident         ::m.rate-sources/id
   :initial-state {::rate-source             {::m.rate-sources/id nil}
                   ::m.rate-sources/name     ""
                   ::m.rate-sources/currency {}
                   ::m.rate-sources/id       nil
                   ::m.rate-sources/url      ""}
   :query         [{::rate-source (comp/get-query u.links/RateSourceLink)}
                   {::m.rate-sources/currency (comp/get-query u.links/CurrencyLink)}
                   ::m.rate-sources/id
                   ::m.rate-sources/name
                   ::m.rate-sources/url]}
  (dom/tr {}
    (dom/td (u.links/ui-rate-source-link rate-source))
    (dom/td url)
    (dom/td (u.links/ui-currency-link currency))
    (dom/td (u.buttons/ui-delete-rate-source-button {::m.rate-sources/id id}))))

(def ui-admin-index-rate-source-line
  (comp/factory AdminIndexRateSourceLine {:keyfn ::m.rate-sources/id}))

(defsc AdminIndexRateSources
  [_this {::keys [rate-sources]}]
  {:ident             (fn [_] [:component/id ::AdminIndexRateSources])
   :initial-state     {::rate-sources  []}
   :query             [:component/id
                       {::rate-sources (comp/get-query AdminIndexRateSourceLine)}]}
  (bulma/box
   (dom/h2 :.title.is-2
     (tr [:rate-sources]))
   (dom/hr)
   (if (empty? rate-sources)
     (dom/p (tr [:no-rate-sources]))
     (dom/table :.table.is-fullwidth.ui
       (dom/thead {}
         (dom/tr {}
           (dom/th "name")
           (dom/th "url")
           (dom/th "currency")
           (dom/th "actions")))
       (dom/tbody {}
         (map ui-admin-index-rate-source-line rate-sources))))))

(def ui-section (comp/factory AdminIndexRateSources))
