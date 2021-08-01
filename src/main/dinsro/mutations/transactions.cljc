(ns dinsro.mutations.transactions
  (:require
   [clojure.spec.alpha :as s]
   #?(:clj [com.fulcrologic.guardrails.core :refer [>defn =>]])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]])
   [dinsro.model.transactions :as m.transactions]
   #?(:clj [dinsro.queries.transactions :as q.transactions])
   [taoensso.timbre :as log]))

(comment ::m.transactions/_)

(s/def ::creation-response (s/keys))

#?(:clj
   (>defn do-create
     [params]
     [::m.transactions/params => ::m.transactions/id]
     (q.transactions/create-record params)))

#?(:cljs
   (defmutation create! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:cljs
   (defmutation delete! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (def resolvers []))
