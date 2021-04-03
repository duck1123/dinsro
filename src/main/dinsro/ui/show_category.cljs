(ns dinsro.ui.show-category
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.categories :as m.categories]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [taoensso.timbre :as timbre]))

(def form-toggle-sm ::form-toggle)

(defsc ShowCategory
  [_this {::m.categories/keys [id name]}]
  {:query [::m.categories/id ::m.categories/name]
   :ident ::m.categories/id
   :initial-state {::m.categories/id 0
                   ::m.categories/name ""}}
  (dom/div
   (dom/p name)
   (dom/p id)
   (u.buttons/ui-delete-category-button {::m.categories/id id})))

(def ui-show-category (comp/factory ShowCategory))
