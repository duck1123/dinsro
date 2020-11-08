(ns dinsro.ui.show-currency
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.specs.currencies :as s.currencies]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]))

(defn show-currency
  [store currency]
  (let [name (::s.currencies/name currency)]
    [:<>
     [:p name]
     (u.debug/hide store [u.buttons/delete-currency store currency])]))

(s/fdef show-currency
  :args (s/cat :currency ::s.currencies/item)
  :ret vector?)
