(ns dinsro.model
  (:require
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.rates :as m.rates]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   [taoensso.timbre :as timbre]))

(def schemata
  [m.accounts/schema
   m.categories/schema
   m.currencies/schema
   m.rates/schema
   m.rate-sources/schema
   m.transactions/schema
   m.users/schema])

(def all-attributes [])
