(ns dinsro.translations
  (:require [taoensso.tempura :as tempura]))

(def dictionary
  {
   :missing {:missing "Missing"}

   :en
   {:missing "Missing text"
    :about "About"
    :actions "Actions"
    :accounts "Accounts"
    :currincies "Currencies"
    :currency "Currency"
    :currency-label "Currency: %1"
    :date "Date"
    :delete "Delete"
    :email-label "Email: %1"
    :id-label "Id: %1"
    :index-accounts "Index Accounts"
    :initial-value-label "Initial Value: %1"
    :login "Login"
    :logout "Logout"
    :name-label "Name: %1"
    :no-accounts "No Accounts"
    :no-currencie "No Currencies"
    :no-rates "No Rates"
    :no-users "No Users"
    :not-loaded "Not Loaded"
    :rates "Rates"
    :register "Register"
    :settings "Settings"
    :user "User"
    :users "Users"
    :user-label "User: %1"
    :value "Value"}})

(def opts {:dict dictionary})

(def tr (partial tempura/tr opts [:missing]))
