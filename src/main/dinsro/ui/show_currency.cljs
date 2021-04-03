(ns dinsro.ui.show-currency
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [taoensso.timbre :as timbre]))

(def form-toggle-sm ::form-toggle)

(defsc ShowCurrency
  [_this {::m.currencies/keys [id name]}]
  {:query [::m.currencies/id ::m.currencies/name]
   :ident ::m.currencies/id
   :initial-state {::m.currencies/id 0
                   ::m.currencies/name ""}}
  (dom/div
   (dom/p name)
   (dom/p id)
   (u.buttons/ui-delete-currency-button {::m.currencies/id id})))

(def ui-show-currency (comp/factory ShowCurrency))
