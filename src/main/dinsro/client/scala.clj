(ns dinsro.client.scala
  "Scala interop helpers"
  (:require
   [clojure.core.async :as async :refer [>!!]]
   [lambdaisland.glogc :as log])
  (:import
   java.util.concurrent.ArrayBlockingQueue
   java.util.concurrent.BlockingQueue
   java.util.concurrent.TimeUnit
   java.util.concurrent.Executor
   java.util.concurrent.ThreadPoolExecutor
   scala.collection.immutable.Vector
   scala.concurrent.ExecutionContext
   scala.Function1
   scala.concurrent.Future
   scala.util.Success))

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

(defn get-work-queue
  []
  (ArrayBlockingQueue. 5))

(defn get-time-unit
  []
  TimeUnit/SECONDS)

(defn get-executor
  ([]
   (get-executor 5 5))
  ([pool-size max-pool-size]
   (get-executor pool-size max-pool-size 5 TimeUnit/SECONDS (get-work-queue)))
  ([pool-size
    max-pool-size
    keep-alive-time
    ^TimeUnit unit
    ^BlockingQueue work-queue]
   (log/info :get-executor/starting
             {:pool-size       pool-size
              :max-pool-size   max-pool-size
              :keep-alive-time keep-alive-time
              :unit            unit
              :work-queue      work-queue})
   (ThreadPoolExecutor. pool-size max-pool-size keep-alive-time unit work-queue)))

(defn get-execution-context
  ([]
   #_(ExecutionContext/global)
   (get-execution-context (get-executor)))
  ([^Executor executor]
   (ExecutionContext/fromExecutor executor)))

(defn await-future
  "Returns a channel representing a scala future"
  ([^Future f] (await-future f (get-execution-context)))
  ([^Future f context]
   (let [ch (async/chan)
         handler
         (reify Function1
           (apply [_this try-obj]
             (log/info :await-future/applied {:try-obj try-obj})
             (if (instance? Success try-obj)
               (let [response (try (.get try-obj) (catch Exception ex ex))]
                 (log/info :await-future/got {:response response})
                 (let [data {:passed true :result response}]
                   (>!! ch data)))
               (let [data {:passed false :result try-obj}]
                 (log/info :await-future/not-success {:data data})
                 (>!! ch data)))))]

     (log/info :await-future/awaiting {:f f})
     (.onComplete f handler context)
     ch)))

(comment

  (def executor (get-executor))

  (def context (get-execution-context executor))
  context

  nil)
