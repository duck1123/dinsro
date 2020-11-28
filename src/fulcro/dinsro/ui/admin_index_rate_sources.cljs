(ns dinsro.ui.admin-index-rate-sources
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.admin-create-rate-source :as u.f.admin-create-rate-source]
   [dinsro.translations :refer [tr]]))

(defsc AdminIndexRateSourceLine
  [_this {::m.rate-sources/keys [id name url currency-id]
          :keys [button-data]}]
  {:query [::m.rate-sources/id
           ::m.rate-sources/name
           ::m.rate-sources/url
           ::m.rate-sources/currency-id
           :button-data]
   :initial-state {::m.rate-sources/id 0
                   ::m.rate-sources/name "unloaded"}
   :ident ::m.rate-sources/id}
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
  [_this {:keys [button-data form-data rate-sources]}]
  {:query [:button-data :form-data :rate-sources]
   :initial-state {:rate-sources []
                   :form-data {}
                   :button-data {}}}
  (dom/div
   :.box
   (dom/h2
    :.title.is-2
    (tr [:rate-sources])
    (u.buttons/ui-show-form-button button-data))
   (u.f.admin-create-rate-source/ui-admin-create-rate-source-form form-data)
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
       (map ui-admin-index-rate-source-line rate-sources))))))
