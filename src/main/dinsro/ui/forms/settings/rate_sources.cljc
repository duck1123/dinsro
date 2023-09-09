(ns dinsro.ui.forms.settings.rate-sources
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.mutations.rate-sources :as mu.rate-sources]))

(def model-key ::m.rate-sources/id)

(def run-button
  {:type   :button
   :local? true
   :label  "Run"
   :action
   (fn [this _key]
     (let [{id model-key} (comp/props this)]
       (comp/transact! this [`(mu.rate-sources/run-query! {~model-key ~id})])))})

(form/defsc-form NewForm
  [_this _props]
  {fo/action-buttons (concat [::run] form/standard-action-buttons)
   fo/attributes     [m.rate-sources/name
                      m.rate-sources/url
                      m.rate-sources/active?
                      m.rate-sources/path]
   fo/cancel-route   ["rate-sources"]
   fo/controls       (merge form/standard-controls {::run run-button})
   fo/id             m.rate-sources/id
   fo/route-prefix   "new-rate-source"
   fo/title          "New Rate Source"})
