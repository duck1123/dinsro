(ns dinsro.model.ln-nodes
  (:refer-clojure :exclude [name])
  (:require
   #?(:clj [clojure.java.io :as io])
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.users :as m.users]
   [taoensso.timbre :as log]))

(def cert-base "/mnt/certs/")

(>defn cert-path
  [id]
  [uuid? => string?]
  (str cert-base id "/tls.cert"))

(>defn macaroon-path
  [id]
  [uuid? => string?]
  (str cert-base id "/admin.macaroon"))

(s/def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::name string?)
(defattr name ::name :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::host string?)
(defattr host ::host :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::port string?)
(defattr port ::port :string
  {ao/identities #{::id}
   ao/schema     :production})

(defattr mnemonic ::mnemonic :vector
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::user ::m.users/id)
(defattr user ::user :ref
  {ao/identities #{::id}
   ao/target     ::m.users/id
   ao/schema     :production})

(defattr hasCert? ::hasCert? :boolean
  {ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-output  [::hasCert?]
   ao/pc-resolve (fn [_env {::keys [id]}]
                   {::hasCert?
                    #?(:clj (.exists (io/file (cert-path id)))
                       :cljs (do (comment id) false))})})

(defattr hasMacaroon? ::hasMacaroon? :boolean
  {ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-output  [::hasMacaroon?]
   ao/pc-resolve (fn [_env {::keys [id]}]
                   {::hasMacaroon?
                    #?(:clj (.exists (io/file (macaroon-path id)))
                       :cljs (do (comment id) false))})})

(defattr unlocked? ::unlocked? :boolean
  {ao/identities #{::id}
   ao/schema     :production})

(defattr initialized? ::initialized? :boolean
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::required-params (s/keys :req [::name ::host ::port]))
(s/def ::params  (s/keys :req [::name ::host ::port ::user]))
(s/def ::item (s/keys :req [::id ::name ::host ::port ::user]))
(s/def ::items (s/coll-of ::item))
(s/def ::ident (s/tuple keyword? ::id))

(>defn ident
  [id]
  [::id => ::ident]
  [::id id])

(>defn ident-item
  [{::keys [id]}]
  [::item => ::ident]
  (ident id))

#?(:clj
   (defn cert-file
     [id]
     (io/file (cert-path id))))

(def attributes
  [id name user host port mnemonic hasCert? hasMacaroon? unlocked? initialized?])
