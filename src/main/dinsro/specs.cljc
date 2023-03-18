(ns dinsro.specs
  (:refer-clojure :exclude [instance?])
  (:require
   [clojure.core.async]
   [clojure.spec.alpha :as s]
   [clojure.spec.gen.alpha :as gen]
   [clojure.test.check.generators]
   [tick.alpha.api :as tick])
  #?(:clj
     (:import
      clojure.core.async.impl.channels.ManyToManyChannel)))

(def default-timezone "America/Detroit")

(defn gen-key
  [key]
  (gen/generate (s/gen key)))

(defn instance?
  [c]
  (partial clojure.core/instance? c))

#?(:clj
   (defn channel?
     [c]
     (clojure.core/instance? ManyToManyChannel c)))

(defn ->inst
  [s]
  (tick/instant (tick/in (tick/date-time s) default-timezone)))

(s/def ::id uuid?)
(s/def :xt/id ::id)

(s/def ::valid-double (s/and double? #(== % %)))
(def valid-double ::valid-double)

(s/def ::date-string (s/with-gen string? #(s/gen #{(str (tick/instant))})))

(s/def ::date (s/with-gen any? #(gen/fmap tick/instant (s/gen ::date-string))))
(def date ::date)

(s/def ::id-string (s/with-gen (s/and string? #(re-matches #"\d+" %))
                     #(gen/fmap str (s/gen pos-int?))))
