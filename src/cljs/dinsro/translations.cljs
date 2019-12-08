(ns dinsro.translations
  (:require [taoensso.tempura :as tempura]))

(def dictionary
  {
   :missing {:missing "Missing"}

   :en
   {:missing "Missing text"
    :currency "Currency"
    :currency-label "Currency: %1"
    :delete "Delete"
    :index-accounts "Index Accounts"
    :initial-value-label "Initial Value: %1"
    :no-accounts "No Accounts"
    :not-loaded "Not Loaded"
    :user "User"
    :user-label "User: %1"}})

(def opts {:dict dictionary})

(def tr (partial tempura/tr opts [:missing]))
