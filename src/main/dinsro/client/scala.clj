(ns dinsro.client.scala
  "Scala interop helpers"
  (:require
   [clojure.core.async :as async :refer [>!!]]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log])
  (:import
   java.util.concurrent.ArrayBlockingQueue
   java.util.concurrent.BlockingQueue
   java.util.concurrent.TimeUnit
   java.util.concurrent.Executor
   java.util.concurrent.ThreadPoolExecutor
   org.bitcoins.core.number.Int32
   org.bitcoins.core.number.UInt32
   org.bitcoins.core.number.UInt64
   org.bitcoins.crypto.DoubleSha256DigestBE
   scala.collection.immutable.Vector
   scala.concurrent.ExecutionContext
   scala.Function1
   scala.concurrent.Future
   scala.Option
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

(defn big-decimal
  [v]
  (scala.math.BigDecimal. (BigDecimal. v)))

(defn int32
  [v]
  (Int32/fromNativeNumber (int v)))

(defn uint32
  [v]
  (UInt32/fromNativeNumber (int v)))

(defn uint64
  [v]
  (UInt64/fromNativeNumber (int v)))

;; https://bitcoin-s.org/api/org/bitcoins/crypto/DoubleSha256DigestBE.html


(>defn double-sha256-digest-be
  "creates a double sha256 digest from hex"
  [v]
  [string? => (ds/instance? DoubleSha256DigestBE)]
  (DoubleSha256DigestBE/fromHex v))

;; https://www.scala-lang.org/api/2.13.8/scala/Option.html#scala.Option

(defn option
  [v]
  (Option/apply v))

(defn none
  []
  (Option/empty))

(defn get-or-nil
  [^Option o]
  (when-not (.isEmpty o) (.get o)))

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
   (log/finer :get-executor/starting
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
             (log/finer :await-future/applied {:try-obj try-obj})
             (if (instance? Success try-obj)
               (let [response (try (.get try-obj) (catch Exception ex ex))]
                 (log/finer :await-future/got {:response response})
                 (let [data {:passed true :result response}]
                   (>!! ch data)))
               (let [data {:passed false :result try-obj}]
                 (log/finer :await-future/not-success {:data data})
                 (>!! ch data)))))]

     (log/finer :await-future/awaiting {:f f})
     (.onComplete f handler context)
     ch)))

(defprotocol Recordable
  (->record [this]))

(comment

  (def executor (get-executor))

  (def context (get-execution-context executor))
  context

  nil)
