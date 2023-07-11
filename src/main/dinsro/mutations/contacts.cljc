(ns dinsro.mutations.contacts
  (:require
   #?(:cljs [com.fulcrologic.fulcro.algorithms.normalized-state :as fns])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.contacts :as m.contacts]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.processors.contacts :as p.contacts])
   [dinsro.responses.contacts :as r.contacts]))

;; [[../processors/contacts.clj]]
;; [[../responses/contacts.cljc]]

(def id-key ::m.contacts/id)

#?(:cljs (comment ::mu/_ ::pc/_))

#?(:clj
   (pc/defmutation create!
     [env props]
     {::pc/params #{::m.contacts/id}
      ::pc/output [::mu/status ::mu/errors ::r.contacts/created-records]}
     (p.contacts/create! env props))

   :cljs
   (fm/defmutation create! [_props]
     (action [_env] true)
     (ok-action [{:keys [state] :as env}]
       (doseq [record (get-in env [:result :body `delete! ::r.contacts/created-records])]
         (swap! state fns/remove-entity [id-key (id-key record)])))
     (remote [env]
       (fm/returning env r.contacts/CreateResponse))))

#?(:clj
   (pc/defmutation delete!
     [env props]
     {::pc/params #{::m.contacts/id}
      ::pc/output [::mu/status ::mu/errors ::r.contacts/deleted-records]}
     (p.contacts/delete! env props))

   :cljs
   (fm/defmutation delete! [_props]
     (action [_env] true)
     (ok-action [{:keys [state] :as env}]
       (doseq [record (get-in env [:result :body `delete! ::r.contacts/deleted-records])]
         (swap! state fns/remove-entity [id-key (id-key record)])))
     (remote [env]
       (fm/returning env r.contacts/DeleteResponse))))

#?(:clj (def resolvers [create! delete!]))
