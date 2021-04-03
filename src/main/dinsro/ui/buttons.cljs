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

(defsc DeleteAccountButton
  [this props]
  {:query []}
  (dom/button
   :.button.is-danger
   {:onClick #(comp/transact! this [(mutations/delete-account props)])}
   "Delete Account"))

(def ui-delete-account-button (comp/factory DeleteAccountButton))

(defsc DeleteCategoryButton
  [this props]
  {:query []}
  (dom/button
   :.button.is-danger
   {:onClick #(comp/transact! this [(mutations/delete-category props)])}
   "Delete Category"))

(def ui-delete-category-button (comp/factory DeleteCategoryButton))

(defsc DeleteCurrencyButton
  [this props]
  {:query []}
  (dom/button
   :.button.is-danger
   {:onClick #(comp/transact! this [(mutations/delete-currency props)])}
   "Delete Currency"))

(def ui-delete-currency-button (comp/factory DeleteCurrencyButton))

(defsc DeleteRateButton
  [this props]
  {:query []}
  (dom/button
   :.button.is-danger
   {:onClick #(comp/transact! this [(mutations/delete-rate props)])}
   "Delete Rate"))

(def ui-delete-rate-button (comp/factory DeleteRateButton))

(defsc DeleteRateSourceButton
  [this props]
  {:query []}
  (dom/button
   :.button.is-danger
   {:onClick #(comp/transact! this [(mutations/delete-rate-source props)])}
   "Delete Rate Source"))

(def ui-delete-rate-source-button (comp/factory DeleteRateSourceButton))

(defsc DeleteTransactionButton
  [this props]
  {:query []}
  (dom/button
   :.button.is-danger
   {:onClick #(comp/transact! this [(mutations/delete-transaction props)])}
   "Delete Transaction"))

(def ui-delete-transaction-button (comp/factory DeleteRateSourceButton))

(defsc DeleteUserButton
  [this props]
  {:query []}
  (dom/button
   :.button.is-danger
   {:onClick #(comp/transact! this [(mutations/delete-user props)])}
   "Delete User"))

(def ui-delete-user-button (comp/factory DeleteUserButton))
