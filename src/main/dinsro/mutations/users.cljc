(ns dinsro.mutations.users
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.users :as a.users])
   [dinsro.model.users :as m.users]
   [dinsro.mutations :as mu]
   [dinsro.specs.users :as s.users]))

#?(:cljs (comment ::pc/_ ::m.users/_))

(defsc DeleteResponse
  [_ _]
  {:initial-state {::mu/status          :initial
                   ::mu/errors          {}}
   :query         [{::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status
                   ::s.users/deleted-records]})

#?(:clj
   (pc/defmutation delete!
     [env props]
     {::pc/params #{::m.users/id}
      ::pc/output [::mu/status ::mu/errors ::s.users/deleted-records]}
     (a.users/do-delete! env props))

   :cljs
   (defmutation delete! [_props]
     (action [_env] true)
     (ok-action [env]
       (let [body     (get-in env [:result :body])
             response (get body `delete!)]
         response))

     (remote [env]
       (fm/returning env DeleteResponse))))

#?(:clj (def resolvers [delete!]))
