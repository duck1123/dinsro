(ns dinsro.model.ln.nodes
  (:refer-clojure :exclude [name])
  (:require
   #?(:clj [clojure.java.io :as io])
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   #?(:clj [dinsro.components.config :refer [config]])
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.users :as m.users]
   #?(:clj [dinsro.specs :as ds]))
  #?(:clj (:import java.io.File)))

(>def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

#?(:clj
   (defn cert-base
     []
     (or (config ::cert-base)
         (throw (RuntimeException. "Cert base not defined")))))

#?(:clj
   (>defn data-path
     [id]
     [::id => string?]
     (str (cert-base) "/" id)))

#?(:clj
   (>defn cert-path
     [id]
     [::id => string?]
     (str (data-path id) "/tls.cert")))

#?(:clj
   (>defn macaroon-path
     [id]
     [::id => string?]
     (str (data-path id) "/admin.macaroon")))

(>def ::name string?)
(defattr name ::name :string
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::c.node ::m.c.nodes/id)
(defattr core-node ::core-node :ref
  {ao/identities #{::id}
   ao/target     ::m.c.nodes/id
   ao/schema     :production
   ::report/column-EQL {::core-node [::m.c.nodes/id ::m.c.nodes/name]}})

(>def ::host string?)
(defattr host ::host :string
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::port string?)
(defattr port ::port :string
  {ao/identities #{::id}
   ao/schema     :production})

(defattr mnemonic ::mnemonic :vector
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::user ::m.users/id)
(defattr user ::user :ref
  {ao/identities #{::id}
   ao/target     ::m.users/id
   ao/schema     :production
   ::report/column-EQL {::user [::m.users/id ::m.users/name]}})

#?(:clj
   (>defn has-cert?
     [id]
     [::id => boolean?]
     (.exists (io/file (cert-path id)))))

#?(:clj
   (>defn has-macaroon?
     [id]
     [::id => boolean?]
     (.exists (io/file (macaroon-path id)))))

(defattr hasCert? ::hasCert? :boolean
  {ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-output  [::hasCert?]
   ao/pc-resolve (fn [_env {::keys [id]}]
                   {::hasCert?
                    #?(:clj (has-cert? id)
                       :cljs (do (comment id) false))})})

(defattr hasMacaroon? ::hasMacaroon? :boolean
  {ao/identities #{::id}
   ao/pc-input   #{::id}
   ao/pc-output  [::hasMacaroon?]
   ao/pc-resolve (fn [_env {::keys [id]}]
                   {::hasMacaroon?
                    #?(:clj (has-macaroon? id)
                       :cljs (do (comment id) false))})})

(defattr unlocked? ::unlocked? :boolean
  {ao/identities #{::id}
   ao/schema     :production})

(defattr initialized? ::initialized? :boolean
  {ao/identities #{::id}
   ao/schema     :production})

(>def ::required-params (s/keys :req [::name ::host ::port ::core-node]))
(>def ::params  (s/keys :req [::name ::host ::port ::user ::core-node]))
(>def ::item (s/keys :req [::id ::name ::host ::port ::user ::core-node]))
(>def ::items (s/coll-of ::item))
(>def ::ident (s/tuple keyword? ::id))

(>defn ident
  [id]
  [::id => any?]
  {::id id})

(>defn ident-item
  [{::keys [id]}]
  [::item => any?]
  (ident id))

(>defn idents
  [ids]
  [(s/coll-of ::id) => any?]
  (mapv ident ids))

#?(:clj
   (>defn cert-file
     [id]
     [::id => (ds/instance? File)]
     (io/file (cert-path id))))

#?(:clj
   (>defn macaroon-file
     [id]
     [::id => (ds/instance? File)]
     (io/file (macaroon-path id))))

(def attributes
  [id name user host port mnemonic hasCert? hasMacaroon? unlocked? initialized? core-node])
