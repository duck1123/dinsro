(ns dinsro.mutations.transactions
  (:require
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]])
   [taoensso.timbre :as log]))

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
