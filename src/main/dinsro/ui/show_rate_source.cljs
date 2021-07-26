(ns dinsro.ui.show-rate-source
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [taoensso.timbre :as log]))

(def form-toggle-sm ::form-toggle)

(defsc ShowRateSource
  [_this {::m.rate-sources/keys [id name]}]
  {:query         [::m.rate-sources/id ::m.rate-sources/name]
   :ident         ::m.rate-sources/id
   :initial-state {::m.rate-sources/id   nil
                   ::m.rate-sources/name ""}}
  (dom/div {}
    (dom/p name)
    (dom/p id)
    (u.buttons/ui-delete-rate-source-button {::m.rate-sources/id id})))

(def ui-show-rate-source (comp/factory ShowRateSource))
