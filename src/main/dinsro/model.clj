(ns dinsro.model
  (:require
   [com.fulcrologic.rad.attributes :as attr]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.core-nodes :as m.core-nodes]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.joins :as m.joins]
   [dinsro.model.ln-nodes :as m.ln-nodes]
   [dinsro.model.ln-info :as m.ln-info]
   [dinsro.model.ln-invoices :as m.ln-invoices]
   [dinsro.model.ln-payments :as m.ln-payments]
   [dinsro.model.ln-peers :as m.ln-peers]
   [dinsro.model.ln-transactions :as m.ln-tx]
   [dinsro.model.navlink :as m.navlink]
   [dinsro.model.rates :as m.rates]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.settings :as m.settings]
   [dinsro.model.timezone :as m.timezone]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   [dinsro.resolvers :as resolvers]
   [taoensso.timbre :as log]))

(def schemata [])

(def all-attributes
  (vec (concat
        m.accounts/attributes
        m.categories/attributes
        m.core-nodes/attributes
        m.currencies/attributes
        m.joins/attributes
        m.ln-info/attributes
        m.ln-invoices/attributes
        m.ln-nodes/attributes
        m.ln-payments/attributes
        m.ln-peers/attributes
        m.ln-tx/attributes
        m.navlink/attributes
        m.rates/attributes
        m.rate-sources/attributes
        m.settings/attributes
        m.timezone/attributes
        m.transactions/attributes
        m.users/attributes
        resolvers/attributes)))

(def all-attribute-validator (attr/make-attribute-validator all-attributes))
