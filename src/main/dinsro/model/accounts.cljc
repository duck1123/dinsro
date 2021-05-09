(ns dinsro.model.accounts
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   #?(:cljs [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]])
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   #?(:clj [com.wsscode.pathom.connect :as pc :refer [defmutation]])
   #?(:clj [dinsro.model.authorization :as exauth])
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.users :as m.users]
   [dinsro.specs]
   [taoensso.timbre :as log]))

(s/def ::ident (s/tuple keyword? ::id))

(>defn ident
  [id]
  [::id => ::ident]
  [::id id])

(>defn ident-item
  [{::keys [id]}]
  [::item => ::ident]
  (ident id))

(s/def ::id string?)
(def id-spec
  {:db/ident       ::id
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one
   :db/unique      :db.unique/identity})

(s/def ::name string?)
(def name-spec
  {:db/ident       ::name
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one})

(s/def ::initial-value (s/or :double double? :zero zero?))

(def initial-value-spec
  {:db/ident       ::initial-value
   :db/valueType   :db.type/number
   :db/cardinality :db.cardinality/one})

(s/def ::currency-id (s/or :id :db/id :zero zero?))
(s/def ::currency
  (s/or :map (s/keys :opt [:db/id ::m.currencies/id])
        :idents (s/coll-of ::m.currencies/ident)))

(def currency-spec
  {:db/ident       ::currency
   :db/valueType   :db.type/ref
   :db/cardinality :db.cardinality/one})

(s/def ::user-id :db/id)

(s/def ::user
  (s/or :map    (s/keys :opt [:db/id ::m.users/id])
        :idents (s/coll-of ::m.users/ident)))

(def user-spec
  {:db/ident       ::user
   :db/valueType   :db.type/ref
   :db/cardinality :db.cardinality/one})

(s/def ::required-params
  (s/keys :req [::name
                ::initial-value]))
(def required-params
  "Required params for accounts"
  ::required-params)
(s/def ::params
  (s/keys :req [::name
                ::initial-value]
          :opt [::currency
                ::user]))

(s/def ::item (s/keys :req [::id ::name ::initial-value ::user]
                      :opt [::currency]))

(def item-spec
  {:db/ident        ::item
   :db.entity/attrs [::name ::initial-value ::currency ::user]})

#?(:clj
   (defmutation login [env params]
     {::pc/params #{:username :password}}
     (exauth/login! env params))
   :cljs
   (defmutation login [_params]
     (ok-action
      [{:keys [app state]}]
      (let [{:time-zone/keys [zone-id]
             ::auth/keys     [status]} (some-> state deref ::auth/authorization :local)]
        (if (= status :success)
          (do
            (when zone-id
              (log/info "Setting UI time zone" zone-id)
              (datetime/set-timezone! zone-id))
            (auth/logged-in! app :local))
          (auth/failed! app :local))))
     (error-action
      [{:keys [app]}]
      (log/error "Login failed.")
      (auth/failed! app :local))
     (remote [env]
             (m/returning env auth/Session))))

(def schema
  [currency-spec
   id-spec
   initial-value-spec
   name-spec
   user-spec])

(def attributes [])

#?(:clj
   (def resolvers []))
