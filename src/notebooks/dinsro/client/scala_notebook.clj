^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.client.scala-notebook
  (:require
   [dinsro.client.scala :as cs]
   [dinsro.notebook-utils :as nu]
   [dinsro.specs :as ds]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # Scala Helpers


^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/display-file-links)

;; ## double-sha256-digest-be

(def digest-input "0a5d6b67612efcd122956820cb8ab6e660f14e4da6ea15c55f4fbee7b733d46f")

(def digest (cs/double-sha256-digest-be digest-input))

(.hex digest)

;; ## Option

(cs/option "foo")

(cs/none)

(comment

  (.get (cs/none))

  (.isEmpty (cs/none))
  (.orNull (cs/none))
  (.getOrElse (cs/none) true)

  (cs/get-or-nil (cs/none))

  (cs/get-or-nil (cs/option "foo"))

  nil)
