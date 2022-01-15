(ns dinsro.model.core-tx-out
  (:refer-clojure :exclude [hash sequence time type])
  (:require
   [clojure.set :as set]
   [clojure.spec.alpha :as s]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.core-tx :as m.core-tx]
   [taoensso.timbre :as log]))

(def rename-map
  {:value ::value
   :n     ::n})

(defn prepare-params
  [params]
  (let [{:keys [scriptPubKey]}         params
        {:keys [asm hex type address]} scriptPubKey
        params                         (set/rename-keys params rename-map)
        params                         (merge {::asm asm ::hex hex ::type type ::address address} params)]
    params))

(s/def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::n number?)
(defattr n ::n :int
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::value number?)
(defattr value ::value :int
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::address (s/or :string string? :nil nil?))
(defattr address ::address :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::asm (s/or :string string? :nil nil?))
(defattr asm ::asm :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::hex (s/or :string string? :nil nil?))
(defattr hex ::hex :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::type (s/or :string string? :nil nil?))
(defattr type ::type :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::transaction uuid?)
(defattr transaction ::transaction :ref
  {ao/identities #{::id}
   ao/target     ::m.core-tx/id
   ao/schema     :production})

(s/def ::params
  (s/keys :req [::n ::value ::transaction]
          :opt [::asm ::hex ::type ::address]))
(s/def ::item
  (s/keys :req [::id ::n ::value ::transaction]
          :opt [::asm ::hex ::type ::address]))

(defn idents
  [ids]
  (mapv (fn [id] {::id id}) ids))

(def attributes [id n value transaction asm hex type address])
