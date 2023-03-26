(ns dinsro.mutations.currencies
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.currencies :as a.currencies])
   [dinsro.model.currencies :as m.currencies]
   [dinsro.mutations :as mu]
   [dinsro.specs.currencies :as s.currencies]))

#?(:cljs (comment ::pc/_ ::s.currencies/_ ::m.currencies/_))

(defsc DeleteResponse
  [_ _]
  {:initial-state {::mu/status          :initial
                   ::mu/errors          {}}
   :query         [{::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status
                   ::s.currencies/deleted-records]})

#?(:clj
   (pc/defmutation delete!
     [env props]
     {::pc/params #{::m.currencies/id}
      ::pc/output [::mu/status ::mu/errors ::s.currencies/deleted-records]}
     (a.currencies/do-delete! env props))

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
