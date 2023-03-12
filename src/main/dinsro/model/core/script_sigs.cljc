(ns dinsro.model.core.script-sigs
  (:refer-clojure :exclude [key])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.core.tx-in :as m.c.tx-in]))

(>def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(>def ::key string?)
(defattr key ::key :string
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::value string?)
(defattr value ::value :string
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::tx-in uuid?)
(defattr tx-in ::tx-in :ref
  {ao/identities #{::id}
   ao/target     ::m.c.tx-in/id
   ao/schema     :production})

(>def ::ident (s/keys :req [::id]))
(>defn ident [id] [::id => ::ident] {::id id})
(>defn idents [ids] [(s/coll-of ::id) => (s/coll-of ::ident)] (mapv ident ids))

(def attributes [id key value tx-in])
