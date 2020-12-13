(ns dinsro.ui.admin-index-rate-sources
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.admin-create-rate-source :as u.f.admin-create-rate-source]
   [dinsro.translations :refer [tr]]))

(defsc AdminIndexRateSourceLine
  [_this {::m.rate-sources/keys [currency-id id name url]
          ::keys [button-data]}]
  {:ident ::m.rate-sources/id
   :initial-state {::button-data                {}
                   ::m.rate-sources/currency-id 0
                   ::m.rate-sources/id          0
                   ::m.rate-sources/name        ""
                   ::m.rate-sources/url         ""}
   :query [::button-data
           ::m.rate-sources/currency-id
           ::m.rate-sources/id
           ::m.rate-sources/name
           ::m.rate-sources/url]}
  (dom/tr
   (dom/td id)
   (dom/td name)
   (dom/td url)
   (dom/td currency-id)
   (dom/td
    (u.buttons/ui-delete-button button-data))))

(def ui-admin-index-rate-source-line
  (comp/factory AdminIndexRateSourceLine {:keyfn ::m.rate-sources/id}))

(defsc AdminIndexRateSources
  [_this {::keys [form rate-sources toggle-button]}]
  {:initial-state {::form          {}
                   ::rate-sources  []
                   ::toggle-button {}}
   :query [{::form          (comp/get-query u.f.admin-create-rate-source/AdminCreateRateSourceForm)}
           {::rate-sources  (comp/get-query AdminIndexRateSourceLine)}
           {::toggle-button (comp/get-query u.buttons/ShowFormButton)}]}
  (let [shown? false]
    (bulma/box
     (dom/h2
      :.title.is-2
      (tr [:rate-sources])
      (u.buttons/ui-show-form-button toggle-button))
     (when shown?
       (u.f.admin-create-rate-source/ui-admin-create-rate-source-form form))
     (dom/hr)
     (if (empty? rate-sources)
       (dom/p (tr [:no-rate-sources]))
       (dom/table
        :.table
        (dom/thead
         (dom/tr
          (dom/th "id")
          (dom/th "name")
          (dom/th "url")
          (dom/th "currency")
          (dom/th "actions")))
        (dom/tbody
         (map ui-admin-index-rate-source-line rate-sources)))))))

(def ui-section (comp/factory AdminIndexRateSources))
