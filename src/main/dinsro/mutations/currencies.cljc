(ns dinsro.mutations.currencies
  (:require
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]])
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.processors.currencies :as p.currencies])
   [dinsro.responses.currencies :as r.currencies]))

;; [../processors/currencies.clj]
;; [../responses/currencies.cljc]

#?(:cljs (comment ::pc/_ ::m.currencies/_ ::mu/_))

#?(:clj
   (pc/defmutation delete!
     [env props]
     {::pc/params #{::m.currencies/id}
      ::pc/output [::mu/status ::mu/errors ::r.currencies/deleted-records]}
     (p.currencies/delete! env props))

   :cljs
   (defmutation delete! [_props]
     (action [_env] true)
     (ok-action [env]
       (let [body     (get-in env [:result :body])
             response (get body `delete!)]
         response))

     (remote [env]
       (fm/returning env r.currencies/DeleteResponse))))

#?(:clj (def resolvers [delete!]))
