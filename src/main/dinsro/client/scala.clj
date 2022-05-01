(ns dinsro.client.scala
  "Scala interop helpers"
  (:require
   [lambdaisland.glogc :as log])
  (:import
   scala.collection.immutable.Vector
   scala.concurrent.ExecutionContext
   scala.Function1
   scala.concurrent.Future))

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

(defn await-future
  [^Future f]
  (.onComplete
   f
   (reify Function1
     (apply [_this a]
       (log/info :pf/apply {:a (.get a)})))
   (ExecutionContext/global)))
