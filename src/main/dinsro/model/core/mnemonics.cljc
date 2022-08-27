(ns dinsro.model.core.mnemonics
  (:refer-clojure :exclude [key name])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]))

(>def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(>def ::required-params
  (s/keys))
(>def ::params
  (s/keys))
(>def ::item
  (s/keys
   :req [::id]))

(def attributes [id])
