(ns dinsro.model.nostr.event-filters
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn => ?]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.specs :as ds]))

(>def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(>def ::filter uuid?)
(defattr filter ::filter :ref
  {ao/identities       #{::id}
   ao/target           ::m.n.filters/id
   ao/schema           :production
   ::report/column-EQL {::filter [::m.n.filters/id ::m.n.filters/address]}})

(>def ::event uuid?)
(defattr event ::event :ref
  {ao/identities       #{::id}
   ao/target           ::m.n.events/id
   ao/schema           :production
   ::report/column-EQL {::event [::m.n.events/id ::m.n.events/address]}})
