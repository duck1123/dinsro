(ns dinsro.mutations.rate-sources
  (:require
   [clojure.spec.alpha :as s]
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.rate-sources :as a.rate-sources])
   [dinsro.model.rate-sources :as m.rate-sources]))

(comment ::pc/_)

(s/def ::item (s/coll-of ::m.rate-sources/ident))
(s/def ::status #{:success :failure :no-user})
(s/def ::create-response (s/keys :req-un [::status]
                                 :opt-un [::item]))

#?(:clj
   (defn do-run!
     [id]
     (a.rate-sources/run-query! id)
     {:status :success}))

#?(:clj
   (pc/defmutation run-query!
     [_request {::m.rate-sources/keys [id]}]
     {::pc/params #{::m.rate-sources/id}
      ::pc/output [:status]}
     (do-run! id))
   :cljs
   (fm/defmutation run-query! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (def resolvers [run-query!]))
