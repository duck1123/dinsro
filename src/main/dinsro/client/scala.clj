(ns dinsro.client.scala
  "Scala interop helpers"
  (:import
   scala.collection.immutable.Vector))

(defn vector->vec
  "Convert a Scala Vector to a Clojure vector"
  [^Vector v]
  (vec (.vectorSlice v 0)))

(defn create-vector
  "Convert a Clojure seq into a Scala Vector"
  [s]
  (let [builder (Vector/newBuilder)]
    (doseq [si s]
      (.addOne builder si))
    (.result builder)))
