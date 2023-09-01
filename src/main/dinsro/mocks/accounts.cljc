(ns dinsro.mocks.accounts
  (:require
   [dinsro.mocks.currencies :as mo.currencies]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.specs :as ds]))

(defn make-account
  []
  {::m.accounts/id       (ds/gen-key ::m.accounts/id)
   ::m.accounts/name     (ds/gen-key ::m.accounts/name)
   ::m.accounts/currency (mo.currencies/make-currency)})
