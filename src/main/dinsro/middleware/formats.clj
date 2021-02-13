(ns dinsro.middleware.formats
  (:require
   [cognitect.transit :as transit]
   [luminus-transit.time :as time]
   [muuntaja.core :as m]
   [taoensso.timbre :as timbre])
  (:import
   [com.cognitect.transit WriteHandler TransitFactory]
   [java.io ByteArrayOutputStream OutputStream]
   [java.util.function Function]))

(def instance
  (m/create
   (-> m/default-options
       (update-in
        [:formats "application/transit+json" :decoder-opts]
        (partial merge time/time-deserialization-handlers))
       (update-in
        [:formats "application/transit+json" :encoder-opts]
        (partial merge time/time-serialization-handlers)))))

(deftype DefaultHandler []
  WriteHandler
  (tag [this v] "unknown")
  (rep [this v] (pr-str v)))

(defn writer
  "Creates a writer over the provided destination `out` using
   the specified format, one of: :msgpack, :json or :json-verbose.
   An optional opts map may be passed. Supported options are:
   :handlers - a map of types to WriteHandler instances, they are merged
   with the default-handlers and then with the default handlers
   provided by transit-java.
   :transform - a function of one argument that will transform values before
   they are written."
  ([out type] (writer out type {}))
  ([^OutputStream out type {:keys [handlers transform default-handler]}]
   (if (#{:json :json-verbose :msgpack} type)
     (let [handler-map (merge transit/default-write-handlers handlers)]
       (transit/->Writer
        (TransitFactory/writer (#'transit/transit-format type) out handler-map default-handler
                               (when transform
                                 (reify Function
                                   (apply [_ x]
                                     (transform x)))))))
     (throw (ex-info "Type must be :json, :json-verbose or :msgpack" {:type type})))))

(defn write-transit [x]
  (let [baos (ByteArrayOutputStream.)
        w    (writer baos :json {;; :handlers transit-write-handlers ; use your handlers here
                                 :default-handler (DefaultHandler.)})
        _    (transit/write w x)
        ret  (str baos)]
    (.reset baos)
    ret))
