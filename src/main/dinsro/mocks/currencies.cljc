(ns dinsro.mocks.currencies
  (:require
   [dinsro.model.currencies :as m.currencies]
   [dinsro.specs :as ds]))

(defn make-currency
  []
  {::m.currencies/id   (ds/gen-key ::m.currencies/id)
   ::m.currencies/name (ds/gen-key ::m.currencies/name)})
