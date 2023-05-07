(ns dinsro.mutations.transactions
  (:require
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.processors.transactions :as p.transactions])
   [dinsro.responses.transactions :as r.transactions]))

#?(:cljs (comment ::mu/_ ::pc/_ ::m.transactions/id))

#?(:clj
   (pc/defmutation delete!
     [env props]
     {::pc/params #{::m.transactions/id}
      ::pc/output [::mu/status ::mu/errors ::r.transactions/deleted-records]}
     (p.transactions/delete! env props))

   :cljs
   (fm/defmutation delete! [_props]
     (action [_env] true)
     (ok-action [env]
       (let [body     (get-in env [:result :body])
             response (get body `delete!)]
         response))

     (remote [env]
       (fm/returning env r.transactions/DeleteResponse))))

#?(:clj (def resolvers [delete!]))
