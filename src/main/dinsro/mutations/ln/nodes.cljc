(ns dinsro.mutations.ln.nodes
  (:require
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.ln.accounts :as a.ln.accounts])
   #?(:clj [dinsro.actions.ln.channels :as a.ln.channels])
   #?(:clj [dinsro.actions.ln.nodes :as a.ln.nodes])
   #?(:clj [dinsro.actions.ln.peers :as a.ln.peers])
   #?(:clj [dinsro.actions.ln.transactions :as a.ln.transactions])
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.ln.remote-nodes :as m.ln.remote-nodes]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.queries.ln.nodes :as q.ln.nodes])
   [dinsro.responses.ln.nodes :as r.ln.nodes]
   #?(:clj [lambdaisland.glogc :as log])))

#?(:clj (comment ::r.ln.nodes/_))
#?(:cljs (comment ::m.ln.nodes/_ ::m.ln.remote-nodes/_ ::pc/_ ::mu/_))

#?(:clj
   (pc/defmutation create-peer!
     [_env {::m.ln.nodes/keys [id]}]
     {::pc/params #{::m.ln.nodes/id}
      ::pc/output [::mu/status]}
     (if-let [node (q.ln.nodes/read-record id)]
       (let [host     ""
             pubkey   ""
             response (a.ln.peers/create-peer! node host pubkey)]
         {::mu/status (if (nil? response) :fail :ok)})
       {::mu/status :not-found}))
   :cljs
   (defmutation create-peer! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (pc/defmutation download-cert!
     [_env {::m.ln.nodes/keys [id]}]
     {::pc/params #{::m.ln.nodes/id}
      ::pc/output [::mu/status
                   ::m.ln.nodes/id
                   ::m.ln.nodes/hasCert?
                   {:com.fulcrologic.fulcro.algorithms.form-state/config
                    [{:com.fulcrologic.fulcro.algorithms.form-state/forms-by-ident
                      [:row :table]}
                     {:com.fulcrologic.fulcro.algorithms.form-state/pristine-state
                      [::m.ln.nodes/hasCert?]}]}]}
     (if-let [node (q.ln.nodes/read-record id)]
       (do (a.ln.nodes/download-cert! node)
           {::mu/status           :ok
            ::m.ln.nodes/id       id
            ::m.ln.nodes/hasCert? true})
       {::mu/status           :fail
        ::m.ln.nodes/id       id
        ::m.ln.nodes/hasCert? false
        :com.fulcrologic.fulcro.algorithms.form-state/config
        {:com.fulcrologic.fulcro.algorithms.form-state/forms-by-ident {:row   id
                                                                       :table ::m.ln.nodes/id}
         :com.fulcrologic.fulcro.algorithms.form-state/pristine-state
         {::m.ln.nodes/hasCert? false}}}))
   :cljs
   (defmutation download-cert! [_props]
     (action [_env] true)
     (remote [env] (fm/returning env r.ln.nodes/NodeCert))))

#?(:clj
   (pc/defmutation download-macaroon!
     [_env {::m.ln.nodes/keys [id]}]
     {::pc/params #{::m.ln.nodes/id}
      ::pc/output [::mu/status]}
     (if-let [node (q.ln.nodes/read-record id)]
       (let [response (a.ln.nodes/download-macaroon! node)
             success  (not (nil? response))]
         {::mu/status               (if success :ok :fail)
          ::m.ln.nodes/hasMacaroon? success
          ::m.ln.nodes/id           id})
       {::mu/status     :not-found
        ::m.ln.nodes/id id}))
   :cljs
   (defmutation download-macaroon! [_props]
     (action [_env] true)
     (remote [env] (fm/returning env r.ln.nodes/NodeMacaroonResponse))))

#?(:clj
   (pc/defmutation generate!
     [_env {::m.ln.nodes/keys [id]}]
     {::pc/params #{::m.ln.nodes/id}
      ::pc/output [::mu/status]}
     (if-let [node (q.ln.nodes/read-record id)]
       (let [address (a.ln.nodes/generate! node)]
         {::mu/status :ok :address address})
       {::mu/status :not-found}))
   :cljs
   (defmutation generate! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (pc/defmutation fetch-accounts!
     [_env {::m.ln.nodes/keys [id]}]
     {::pc/params #{::m.ln.nodes/id}
      ::pc/output [::mu/status]}
     (a.ln.accounts/do-fetch-accounts! id))
   :cljs
   (defmutation fetch-accounts! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (pc/defmutation fetch-channels!
     [_env {::m.ln.nodes/keys [id]}]
     {::pc/params #{::m.ln.nodes/id}
      ::pc/output [::mu/status]}
     (a.ln.channels/fetch-channels! id)
     {::mu/status :ok})
   :cljs
   (defmutation fetch-channels! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (pc/defmutation fetch-invoices!
     [_env {::m.ln.nodes/keys [id]}]
     {::pc/params #{::m.ln.nodes/id}
      ::pc/output [::mu/status]}
     (comment id)
     {::mu/status :ok})
   :cljs
   (defmutation fetch-invoices! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (pc/defmutation fetch-payments!
     [_env {::m.ln.nodes/keys [id]}]
     {::pc/params #{::m.ln.nodes/id}
      ::pc/output [::mu/status]}
     (comment id)
     {::mu/status :ok})
   :cljs
   (defmutation fetch-payments! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (pc/defmutation fetch-peers!
     [_env {::m.ln.nodes/keys [id] :as props}]
     {::pc/params #{::m.ln.nodes/id}
      ::pc/output [::mu/status]}
     (if id
       (do
         (log/info :fetch-peers!/starting {:props props})
         (a.ln.peers/fetch-peers! id)
         {::mu/status :ok})
       (do
         (log/error :fetch-peers!/missing-id {})
         (throw (ex-info "Missing id" {})))))

   :cljs
   (defmutation fetch-peers! [_props]
     (action [_env] true)
     (remote [env]
       (-> env
           (fm/returning r.ln.nodes/PeerResponse)))))

#?(:clj
   (pc/defmutation fetch-transactions!
     [_env {::m.ln.nodes/keys [id]}]
     {::pc/params #{::m.ln.nodes/id}
      ::pc/output [::mu/status]}
     (a.ln.transactions/fetch-transactions! id)
     {::mu/status :ok})
   :cljs
   (defmutation fetch-transactions! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (pc/defmutation make-peer!
     [_env {node-id ::m.ln.nodes/id
            remote-node-id ::m.ln.remote-nodes/id}]
     {::pc/params #{::m.ln.nodes/id ::m.ln.remote-nodes/id}
      ::pc/output [::mu/status]}
     (a.ln.peers/make-peer* node-id remote-node-id))
   :cljs
   (defmutation make-peer! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (pc/defmutation initialize!
     [_env {::m.ln.nodes/keys [id]}]
     {::pc/params #{::m.ln.nodes/id}
      ::pc/output [::mu/status]}
     (if-let [node (q.ln.nodes/read-record id)]
       (do (a.ln.nodes/initialize! node)
           {::mu/status "ok"})
       (do
         (log/error :initialize!/no-node {})
         {::mu/status "fail"})))
   :cljs
   (defmutation initialize! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (pc/defmutation update-info!
     [_env {::m.ln.nodes/keys [id]}]
     {::pc/params #{::m.ln.nodes/id}
      ::pc/output [::mu/status]}
     (if-let [node (q.ln.nodes/read-record id)]
       (let [ch (a.ln.nodes/update-info! node)]
         {::mu/status (if (nil? ch) "fail" "pass")})
       (do (log/error :update-info!/no-node {})
           {::mu/status "fail"})))
   :cljs
   (defmutation update-info! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (pc/defmutation update-transactions!
     [_env {::m.ln.nodes/keys [id]}]
     {::pc/params #{::m.ln.nodes/id}
      ::pc/output [::mu/status]}
     (if-let [node (q.ln.nodes/read-record id)]
       (do
         (comment node)
         (throw (ex-info "not implemented" {}))
         {::mu/status "ok"})
       (do (log/error :update-transactions!/no-node {})
           {::mu/status "fail"})))
   :cljs
   (defmutation update-transactions! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (pc/defmutation unlock!
     [_env {::m.ln.nodes/keys [id]}]
     {::pc/params #{::m.ln.nodes/id}
      ::pc/output [::mu/status]}
     (if-let [node (q.ln.nodes/read-record id)]
       (do (a.ln.nodes/unlock! node)
           {::mu/status "ok"})
       (do (log/error :unlock!/no-node {})
           {::mu/status "fail"})))
   :cljs
   (defmutation unlock! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (def resolvers
     [create-peer!
      download-cert!
      download-macaroon!
      fetch-accounts!
      fetch-channels!
      fetch-invoices!
      fetch-payments!
      fetch-peers!
      fetch-transactions!
      generate!
      initialize!
      make-peer!
      update-info!
      update-transactions!
      unlock!]))
