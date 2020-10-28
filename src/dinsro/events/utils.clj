(ns dinsro.events.utils
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.store :as st]
   [taoensso.timbre :as timbre]))

;; Macros

(defmacro declare-fetch-index-method
  [ns-sym]
  `(do
     #_(taoensso.timbre/infof "declaring fetch index method - %s" ~ns-sym)
     (require 'dinsro.events.utils.impl)
     (let [fetch-kw# (keyword ~ns-sym "do-fetch-index-state")
           do-fetch-index-cofx-kw# (keyword ~ns-sym "do-fetch-index-cofx")
           do-fetch-index-event-kw# (keyword ~ns-sym "do-fetch-index-event")
           do-fetch-index-response-kw# (keyword ~ns-sym "do-fetch-index-response")]
       (clojure.spec.alpha/def-impl fetch-kw#
         :dinsro.spec/state
         :dinsro.spec/state)

       (clojure.spec.alpha/def-impl do-fetch-index-cofx-kw#
         (clojure.spec.alpha/keys)
         (clojure.spec.alpha/keys))

       (clojure.spec.alpha/def-impl do-fetch-index-event-kw#
         (clojure.spec.alpha/keys)
         (clojure.spec.alpha/keys))

       (clojure.spec.alpha/def-impl (keyword ~ns-sym "do-fetch-index-response")
         (clojure.spec.alpha/keys)
         (clojure.spec.alpha/keys)))))

(defmacro declare-fetch-record-method
  [ns-sym]
  `(do
     #_(taoensso.timbre/infof "declaring fetch record - %s" ~ns-sym)
     (require 'dinsro.events.utils.impl)
     (let [fetch-kw# (keyword ~ns-sym "do-fetch-record-state")
           do-fetch-record-failed-cofx-kw# (keyword ~ns-sym "do-fetch-record-failed-cofx")
           do-fetch-record-failed-event-kw# (keyword ~ns-sym "do-fetch-record-failed-event")
           do-fetch-record-failed-response-kw# (keyword ~ns-sym "do-fetch-record-failed-response")]

       (clojure.spec.alpha/def-impl fetch-kw#
         :dinsro.spec/state
         :dinsro.spec/state)

       (clojure.spec.alpha/def-impl do-fetch-record-failed-cofx-kw#
         (clojure.spec.alpha/keys)
         (clojure.spec.alpha/keys))

       (clojure.spec.alpha/def-impl do-fetch-record-failed-event-kw#
         (clojure.spec.alpha/keys)
         (clojure.spec.alpha/keys))

       (clojure.spec.alpha/def-impl do-fetch-record-failed-response-kw#
         (clojure.spec.alpha/keys)
         (clojure.spec.alpha/keys)))))

(defmacro declare-delete-record-method
  [_ns-sym]
  `(do
     #_(taoensso.timbre/infof "declaring delete record - %s" ~ns-sym)
     (require 'dinsro.events.utils.impl)))

(defmacro declare-model
  [ns-sym]
  `(do
     (require 'dinsro.events.utils.impl)
     (let [item-key# (keyword ~ns-sym "item")
          items-key# (keyword ~ns-sym "items")
          item-map-key# (keyword ~ns-sym "items")]
      #_(timbre/infof "Declaring model - %s" ~ns-sym)
      (clojure.spec.alpha/def-impl item-map-key#
        (clojure.spec.alpha/map-of :dinsro.spec/id item-key#)
        (clojure.spec.alpha/map-of :dinsro.spec/id item-key#))
      (clojure.spec.alpha/def-impl items-key#
        (clojure.spec.alpha/coll-of item-key#)
        (clojure.spec.alpha/coll-of item-key#)))))

(defmacro declare-form
  [ns-sym
   form-data-spec
   form-defs]
  `(do
     #_(taoensso.timbre/infof "declaring form - %s" ~ns-sym)
     (require 'dinsro.events.utils.impl)
     (clojure.spec.alpha/def-impl
       (keyword ~ns-sym "shown?")
       boolean?
       boolean?)

     (clojure.spec.alpha/def-impl
       (keyword ~ns-sym "form-data")
       ~form-data-spec
       ~form-data-spec)

     (def ~'form-defs ~form-defs)))

(defmacro declare-subform
  [ns-sym
   form-data-spec
   form-defs]
  `(do
     #_(taoensso.timbre/infof "declaring sub form - %s" ~ns-sym)
     (require 'dinsro.events.utils.impl)

     (clojure.spec.alpha/def-impl
       (keyword ~ns-sym "shown?")
       boolean?
       boolean?)

     (clojure.spec.alpha/def-impl
       (keyword ~ns-sym "form-data")
       ~form-data-spec
       ~form-data-spec)

     (def ~'form-defs ~form-defs)))

(defmacro register-fetch-index-method
  [store ns-sym path-selector]
  `(do
     #_(taoensso.timbre/infof "registering index method - %s" ~ns-sym)
     (doto ~store
       (dinsro.store/reg-basic-sub
        (keyword ~ns-sym "do-fetch-index-state"))
       (dinsro.store/reg-event-fx
        (keyword ~ns-sym "do-fetch-index-success")
        (partial dinsro.events.utils.impl/do-fetch-index-success ~ns-sym))
       (dinsro.store/reg-event-fx
        (keyword ~ns-sym "do-fetch-index-failed")
        (partial dinsro.events.utils.impl/do-fetch-index-failed ~ns-sym))
       (dinsro.store/reg-event-fx
        (keyword ~ns-sym "do-fetch-index")
        (partial dinsro.events.utils.impl/do-fetch-index ~ns-sym ~path-selector ~store)))))

(defmacro register-fetch-record-method
  [store ns-sym path-selector]
  `(do
     #_(timbre/infof "Registering fetch method - %s" ~ns-sym)
     (doto ~store
       (dinsro.store/reg-basic-sub
        (keyword ~ns-sym "do-fetch-record-state"))
       (dinsro.store/reg-event-fx
        (keyword ~ns-sym "do-fetch-record-success")
        (partial dinsro.events.utils.impl/do-fetch-record-success ~ns-sym))
       (dinsro.store/reg-event-fx
        (keyword ~ns-sym "do-fetch-record-failed")
        (partial dinsro.events.utils.impl/do-fetch-record-failed ~ns-sym))
       (dinsro.store/reg-event-fx
        (keyword ~ns-sym "do-fetch-record")
        (partial dinsro.events.utils.impl/do-fetch-record ~ns-sym ~path-selector ~store)))))

(defmacro register-delete-record-method
  [store ns-sym path-selector]
  `(do
     #_(timbre/infof "Registering delete method - %s" ~ns-sym)
     (doto ~store
       (dinsro.store/reg-basic-sub
        (keyword ~ns-sym "do-delete-record-state"))
       (dinsro.store/reg-event-fx
        (keyword ~ns-sym "do-delete-record-success")
        (partial dinsro.events.utils.impl/do-delete-record-success ~ns-sym))
       (dinsro.store/reg-event-fx
        (keyword ~ns-sym "do-delete-record-failed")
        (partial dinsro.events.utils.impl/do-delete-record-failed ~ns-sym))
       (dinsro.store/reg-event-fx
        (keyword ~ns-sym "do-delete-record")
        (partial dinsro.events.utils.impl/do-delete-record ~ns-sym ~path-selector ~store)))))

(defmacro register-model-store
  [store ns-sym]
  `(doto ~store
     (dinsro.store/reg-basic-sub (keyword ~ns-sym "item-map"))
     (dinsro.store/reg-sub
      (keyword ~ns-sym "item")
      (partial dinsro.events.utils.impl/item-sub ~ns-sym))
     (dinsro.store/reg-sub
      (keyword ~ns-sym "items")
      (partial dinsro.events.utils.impl/items-sub ~ns-sym))))

(defmacro register-form
  [store ns-sym]
  `(do
     #_(timbre/infof "Registering form - %s" ~ns-sym)
     (doto ~store
       (dinsro.store/reg-basic-sub (keyword ~ns-sym "shown?"))
       (dinsro.store/reg-set-event (keyword ~ns-sym "shown?")))

     (doseq [[out-key# in-key# default#] ~'form-defs]
       #_(timbre/infof "Registering key - %s" in-key#)
       (doto ~store
         (dinsro.store/reg-basic-sub in-key#)
         (dinsro.store/reg-set-event in-key#)))))

(defmacro register-subform
  [store ns-sym]
  `(do
     #_(timbre/infof "Registering sub form - %s" ~ns-sym)
     (doto ~store
       (dinsro.store/reg-basic-sub (keyword ~ns-sym "shown?"))
       (dinsro.store/reg-set-event (keyword ~ns-sym "shown?")))))
