(ns dinsro.specs
  (:refer-clojure :exclude [instance?])
  (:require
   [clojure.core.async]
   [clojure.spec.alpha :as s]
   [clojure.spec.gen.alpha :as gen]
   [clojure.test.check.generators]
   [tick.core :as t])
  (:import
   #?(:clj clojure.core.async.impl.channels.manytomanychannel)
   #?(:clj clojure.lang.atom)
   #?(:clj java.util.date)))

(def default-timezone "america/detroit")

(defn gen-key
  [key]
  (gen/generate (s/gen key)))

(defn instance?
  [c]
  (partial clojure.core/instance? c))

#?(:clj
   (defn channel?
     [c]
     (clojure.core/instance? manytomanychannel c)))

#?(:clj (defn atom? [a] (= (type a) atom)))

(defn ->inst
  ([]
   (t/instant))
  ([s]
   (t/instant (t/in (t/date-time s) default-timezone))))

#?(:clj
   (defn ms->inst
     [ms]
     ;; [number? => t/instant?]
     (Date. ms)))

(s/def ::id uuid?)
(s/def :xt/id ::id)

(s/def ::valid-double (s/and double? #(== % %)))
(def valid-double ::valid-double)

(s/def ::date-string (s/with-gen string? #(s/gen #{(str (t/instant))})))
(s/def ::date (s/with-gen any? #(gen/fmap t/instant (s/gen ::date-string))))
(def date ::date)

(s/def ::id-string (s/with-gen (s/and string? #(re-matches #"\d+" %))
                     #(gen/fmap str (s/gen pos-int?))))

(defn make-rows
  [opts f]
  (let [max-rows  (:max-rows opts 8)
        row-count (:row-count opts (inc (rand-int max-rows)))]
    (mapv (fn [_] (f)) (range row-count))))
