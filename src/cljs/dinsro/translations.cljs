(ns dinsro.translations
  (:require [taoensso.tempura :as tempura]))

(def dictionary
  {
   :missing {:missing "Missing"}

   :en
   {:missing "Missing text"
    :currency "Currency"
    :delete "Delete"
    :index-accounts "Index Accounts"
    :not-loaded "Not Loaded"
    :user "User"}})

(def opts {:dict dictionary})

(def tr (partial tempura/tr opts [:missing]))
