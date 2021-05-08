(ns dinsro.ui.buttons
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [dinsro.mutations.accounts :as mutations.accounts]
   [dinsro.mutations.categories :as mutations.categories]
   [dinsro.mutations.currencies :as mutations.currencies]
   [dinsro.mutations.rates :as mutations.rates]
   [dinsro.mutations.rate-sources :as mutations.rate-sources]
   [dinsro.mutations.transactions :as mutations.transactions]
   [dinsro.mutations.users :as mutations.users]
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
  (dom/a :.is-pulled-right
    {:onClick #(uism/trigger! this id :event/toggle {})}
    (tr [:show-form "Show"])))

(def ui-show-form-button (comp/factory ShowFormButton))

(defsc DeleteAccountButton
  [this props]
  {:query []}
  (dom/button :.button.is-danger.ui
    {:onClick #(comp/transact! this [(mutations.accounts/delete! props)])}
    "Delete Account"))

(def ui-delete-account-button (comp/factory DeleteAccountButton))

(defsc DeleteCategoryButton
  [this props]
  {:query []}
  (dom/button :.button.is-danger.ui
    {:onClick #(comp/transact! this [(mutations.categories/delete! props)])}
    "Delete Category"))

(def ui-delete-category-button (comp/factory DeleteCategoryButton))

(defsc DeleteCurrencyButton
  [this props]
  {:query []}
  (dom/button :.button.is-danger.ui
    {:onClick #(comp/transact! this [(mutations.currencies/delete! props)])}
    "Delete Currency"))

(def ui-delete-currency-button (comp/factory DeleteCurrencyButton))

(defsc DeleteRateButton
  [this props]
  {:query []}
  (dom/button :.button.is-danger.ui
    {:onClick #(comp/transact! this [(mutations.rates/delete! props)])}
    "Delete Rate"))

(def ui-delete-rate-button (comp/factory DeleteRateButton))

(defsc DeleteRateSourceButton
  [this props]
  {:query []}
  (dom/button :.button.is-danger.ui
    {:onClick #(comp/transact! this [(mutations.rate-sources/delete! props)])}
    "Delete Rate Source"))

(def ui-delete-rate-source-button (comp/factory DeleteRateSourceButton))

(defsc DeleteTransactionButton
  [this props]
  {:query []}
  (dom/button :.button.is-danger.ui
    {:onClick #(comp/transact! this [(mutations.transactions/delete! props)])}
    "Delete Transaction"))

(def ui-delete-transaction-button (comp/factory DeleteRateSourceButton))

(defsc DeleteUserButton
  [this props]
  {:query []}
  (dom/button :.button.is-danger.ui
    {:onClick #(comp/transact! this [(mutations.users/delete! props)])}
    "Delete User"))

(def ui-delete-user-button (comp/factory DeleteUserButton))
