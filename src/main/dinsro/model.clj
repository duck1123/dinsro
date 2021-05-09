(ns dinsro.model
  (:require
   [com.fulcrologic.rad.attributes :as attr]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rates :as m.rates]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.settings :as m.settings]
   [dinsro.model.timezone :as m.timezone]
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

(def all-resolvers
  (vec (concat
        m.accounts/resolvers
        m.categories/resolvers
        m.currencies/resolvers
        m.rates/resolvers
        m.rate-sources/resolvers
        m.settings/resolvers
        m.timezone/resolvers
        m.transactions/resolvers
        m.users/resolvers)))

(def all-attributes
  (vec (concat
        m.accounts/attributes
        m.categories/attributes
        m.currencies/attributes
        m.rates/attributes
        m.rate-sources/attributes
        m.settings/attributes
        m.timezone/attributes
        m.transactions/attributes
        m.users/attributes)))

(def all-attribute-validator (attr/make-attribute-validator all-attributes))
