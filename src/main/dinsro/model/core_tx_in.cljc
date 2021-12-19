(ns dinsro.model.core-tx-in
  (:refer-clojure :exclude [hash sequence time])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.core-tx :as m.core-tx]
   [taoensso.timbre :as log]))

(s/def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

;; (s/def ::script-sig number?)
;; (defattr script-sig ::script-sig :ref
;;   {ao/identities #{::id}
;;    ao/schema     :production})

(s/def ::scriptPubKey (s/or :string string? :nil nil?))
(defattr script-pub-key ::script-pub-key :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::sequence number?)
(defattr sequence ::sequence :long
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::transaction uuid?)
(defattr transaction ::transaction :ref
  {ao/identities #{::id}
   ao/target     ::m.core-tx/id
   ao/schema     :production})

(s/def ::txid (s/or :string string? :nil nil?))
(defattr txid ::txid :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::txid (s/or :int number? :nil nil?))
(defattr vout ::vout :int
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::params
  (s/keys :req [::sequence ::transaction]
          :opt [::vout ::script-pub-key ::txid]))
(s/def ::item
  (s/keys :req [::id ::sequence ::transaction]
          :opt [::vout ::script-pub-key ::txid]))

(def attributes [id                ;; script-sig
                 sequence transaction vout script-pub-key txid vout])