(ns dinsro.mutations.user-pubkeys
  (:require
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]])
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.user-pubkeys :as m.user-pubkeys]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.processors.user-pubkeys :as p.user-pubkeys])
   [dinsro.responses.user-pubkeys :as r.user-pubkeys]))

#?(:cljs (comment ::pc/_ ::m.user-pubkeys/_  ::mu/_ ::r.user-pubkeys/_))

#?(:clj
   (pc/defmutation delete!
     [env props]
     {::pc/params #{::m.user-pubkeys/id}
      ::pc/output [::mu/status ::mu/errors ::r.user-pubkeys/deleted-records]}
     (p.user-pubkeys/delete! env props))

   :cljs
   (defmutation delete! [_props]
     (action [_env] true)
     (ok-action [env]
       (let [body     (get-in env [:result :body])
             response (get body `delete!)]
         response))

     (remote [env]
       (fm/returning env r.user-pubkeys/DeleteResponse))))

#?(:clj (def resolvers [delete!]))
