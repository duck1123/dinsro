(ns dinsro.model
  (:require
   [com.fulcrologic.rad.attributes :as attr]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.joins :as m.joins]
   [dinsro.model.navlink :as m.navlink]
   [dinsro.model.rates :as m.rates]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.settings :as m.settings]
   [dinsro.model.timezone :as m.timezone]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   [taoensso.timbre :as log]))

(def schemata [])

(def all-attributes
  (vec (concat
        m.accounts/attributes
        m.categories/attributes
        m.currencies/attributes
        m.joins/attributes
        m.navlink/attributes
        m.rates/attributes
        m.rate-sources/attributes
        m.settings/attributes
        m.timezone/attributes
        m.transactions/attributes
        m.users/attributes)))

(def all-attribute-validator (attr/make-attribute-validator all-attributes))
