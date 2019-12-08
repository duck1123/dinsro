(ns dinsro.translations
  (:require [taoensso.tempura :as tempura]))

(def dictionary
  {:en
   {:missing "Missing text"
    :index-accounts "Index Accounts"}})

(def opts {:dict dictionary})

(def tr (partial tempura/tr opts [:en]))
