(ns dinsro.specs.nostr.pubkeys
  (:require
   [clojure.spec.alpha :as s]
   #?(:clj  [com.fulcrologic.guardrails.core :refer [>def
                                                     =>]]
      :cljs [com.fulcrologic.guardrails.core :refer [>def =>]])
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.mutations :as mu]))

(>def ::fetch!-request
  (s/keys :req [::m.n.pubkeys/id]))

(>def ::fetch!-response-success
  (s/keys :req [::mu/status]))

(>def ::fetch!-response-error
  (s/keys :req [::mu/status]))

(>def ::fetch!-response
  (s/or :success ::fetch!-response-success
        :error ::fetch!-response-error))
