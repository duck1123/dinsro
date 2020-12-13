(ns dinsro.ui.admin-index-transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]))

(defsc AdminIndexTransactions
  [_this {::keys [toggle-button]}]
  {:initial-state {::toggle-button {}}
   :query [{::toggle-button (comp/get-query u.buttons/ShowFormButton)}]}
  (let [items []]
    (bulma/box
     (dom/h2
      :.title.is-2
      "Index Transactions"
      (u.buttons/ui-show-form-button toggle-button))
     (dom/hr)
     (if (seq items)
       (dom/div)
       (dom/p "No data")))))

(def ui-section (comp/factory AdminIndexTransactions))
