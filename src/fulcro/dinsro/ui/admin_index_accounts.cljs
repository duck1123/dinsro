(ns dinsro.ui.admin-index-accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defsc AdminIndexAccountLine
  [_this {:account/keys [id name user-id currency-id initial-value]}]
  (dom/tr
   (dom/td id)
   (dom/td name)
   (dom/td user-id)
   (dom/td currency-id)
   (dom/td initial-value)))

(def ui-admin-index-account-line (comp/factory AdminIndexAccountLine))

(defsc AdminIndexAccounts
  [_this {:keys [accounts]}]
  {:initial-state {:accounts [{:account/name "foo"}]}
   :query [:accounts]}
  (dom/div
   :.box
   (dom/h1 (tr [:index-accounts])
           (dom/button "+"))
   (dom/div "Admin create account form")
   (dom/hr)
   (if (empty? accounts)
     (dom/div (tr [:no-accounts]))
     (dom/table
      (dom/thead
       (dom/th "Id")
       (dom/th (tr [:name]))
       (dom/th (tr [:user-label]))
       (dom/th (tr [:currency-label]))
       (dom/th (tr [:initial-value-label]))
       (dom/th (tr [:buttons])))
      (dom/tbody
       (map ui-admin-index-account-line accounts))))))
