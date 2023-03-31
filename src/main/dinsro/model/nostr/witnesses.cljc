(ns dinsro.model.nostr.witnesses
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn ? =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.model.nostr.runs :as m.n.runs]))

(>def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(>def ::event uuid?)
(defattr event ::event :ref
  {ao/identities       #{::id}
   ao/target           ::m.n.events/id
   ao/schema           :production
   ::report/column-EQL {::event [::m.n.events/id ::m.n.events/note-id]}})

(>def ::run uuid?)
(defattr run ::run :ref
  {ao/identities       #{::id}
   ao/target           ::m.n.runs/id
   ao/schema           :production
   ::report/column-EQL {::runs [::m.n.runs/id ::m.n.runs/status]}})

(>def ::params (s/keys :req [::run ::event]))
(>def ::item (s/keys :req [::id ::run ::event]))

(>def ::ident (s/keys :req [::id]))
(>defn ident [id] [::id => ::ident] {::id id})
(>defn idents [ids] [(s/coll-of ::id) => (s/coll-of ::ident)] (mapv ident ids))

(def attributes [id run event])
