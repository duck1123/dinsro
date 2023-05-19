(ns dinsro.mutations.users
  (:require
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]])
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.users :as m.users]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.processors.users :as p.users])
   [dinsro.responses.users :as r.users]))

#?(:cljs (comment ::pc/_ ::m.users/_ ::mu/_ ::r.users/_))

#?(:clj
   (pc/defmutation delete!
     [env props]
     {::pc/params #{::m.users/id}
      ::pc/output [::mu/status ::mu/errors ::r.users/deleted-records]}
     (p.users/delete! env props))

   :cljs
   (defmutation delete! [_props]
     (action [_env] true)
     (ok-action [env]
       (let [body     (get-in env [:result :body])
             response (get body `delete!)]
         response))

     (remote [env]
       (fm/returning env r.users/DeleteResponse))))

#?(:clj (def resolvers [delete!]))
