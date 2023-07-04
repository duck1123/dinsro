(ns dinsro.model.models
  (:refer-clojure :exclude [name])
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]))

(defattr id ::id :keyword
  {ao/identity? true})

(def attributes
  [id])
