(ns dinsro.ui.show-rate-source
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.semantic-ui.elements.button.ui-button :refer [ui-button]]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(def form-toggle-sm ::form-toggle)

(defsc ShowRateSource
  [_this {::m.rate-sources/keys [id name]}]
  {:query [::m.rate-sources/id ::m.rate-sources/name]
   :ident ::m.rate-sources/id
   :initial-state {::m.rate-sources/id 0
                   ::m.rate-sources/name ""}}
  (dom/div
   (dom/p name)
   (dom/p id)
   (ui-button {:className "button is-danger"
               :content "Delete"})))

(def ui-show-rate-source (comp/factory ShowRateSource))
