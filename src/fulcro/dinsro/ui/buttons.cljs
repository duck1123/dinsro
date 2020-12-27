(ns dinsro.ui.buttons
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [dinsro.mutations :as mutations]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defsc ShowFormButton
  [this {:form-button/keys [id]}]
  {:ident :form-button/id
   :initial-state
   (fn [{:form-button/keys [id]}]
     {:form-button/id id})
   :query [:form-button/id
           :form-button/state]}
  (dom/a
   :.is-pulled-right
   {:onClick #(uism/trigger! this id :event/toggle {})}
   (tr [:show-form "Show"])))

(def ui-show-form-button (comp/factory ShowFormButton))

(defsc DeleteButton
  [this props]
  {:query []}
  (dom/button
   :.button.is-danger
   {:onClick #(comp/transact! this [(mutations/delete props)])}
   "Delete"))

(def ui-delete-button (comp/factory DeleteButton))
