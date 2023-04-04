(ns dinsro.mutations.ln.nodes
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.ln.accounts :as a.ln.accounts])
   #?(:clj [dinsro.actions.ln.channels :as a.ln.channels])
   #?(:clj [dinsro.actions.ln.nodes :as a.ln.nodes])
   #?(:clj [dinsro.actions.ln.peers :as a.ln.peers])
   #?(:clj [dinsro.actions.ln.transactions :as a.ln.transactions])
   [dinsro.model.ln.info :as m.ln.info]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.ln.remote-nodes :as m.ln.remote-nodes]
   #?(:clj [dinsro.queries.ln.nodes :as q.ln.nodes])
   #?(:clj [lambdaisland.glogc :as log])))

(comment ::m.ln.info/_ ::m.ln.nodes/_ ::m.ln.remote-nodes/_ ::pc/_)

#?(:clj
   (pc/defmutation create-peer!
     [_env {::m.ln.nodes/keys [id]}]
     {::pc/params #{::m.ln.nodes/id}
      ::pc/output [:status]}
     (if-let [node (q.ln.nodes/read-record id)]
       (let [host     ""
             pubkey   ""
             response (a.ln.peers/create-peer! node host pubkey)]
         {:status (if (nil? response) :fail :ok)})
       {:status :not-found}))
   :cljs
   (defmutation create-peer! [_props]
     (action [_env] true)
     (remote [_env] true)))

(defsc NodeCert
  [_this _props]
  {:query [::m.ln.nodes/hasCert?
           ::m.ln.nodes/id
           :com.fulcrologic.fulcro.algorithms.form-state/config]
   :ident ::m.ln.nodes/id})

(defsc NodeMacaroonResponse
  [_this _props]
  {:query [::m.ln.nodes/hasMacaroon?
           ::m.ln.nodes/id
           ::status]
   :ident ::m.ln.nodes/id})

#?(:clj
   (pc/defmutation download-cert!
     [_env {::m.ln.nodes/keys [id]}]
     {::pc/params #{::m.ln.nodes/id}
      ::pc/output [:status
                   ::m.ln.nodes/id
                   ::m.ln.nodes/hasCert?
                   {:com.fulcrologic.fulcro.algorithms.form-state/config
                    [{:com.fulcrologic.fulcro.algorithms.form-state/forms-by-ident
                      [:row :table]}
                     {:com.fulcrologic.fulcro.algorithms.form-state/pristine-state
                      [::m.ln.nodes/hasCert?]}]}]}
     (if-let [node (q.ln.nodes/read-record id)]
       (do (a.ln.nodes/download-cert! node)
           {:status               :ok
            ::m.ln.nodes/id       id
            ::m.ln.nodes/hasCert? true})
       {:status               :fail
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
     (remote [env] (fm/returning env NodeCert))))

#?(:clj
   (pc/defmutation download-macaroon!
     [_env {::m.ln.nodes/keys [id]}]
     {::pc/params #{::m.ln.nodes/id}
      ::pc/output [:status]}
     (if-let [node (q.ln.nodes/read-record id)]
       (let [response (a.ln.nodes/download-macaroon! node)
             success  (not (nil? response))]
         {::status                  (if success :ok :fail)
          ::m.ln.nodes/hasMacaroon? success
          ::m.ln.nodes/id           id})
       {::status        :not-found
        ::m.ln.nodes/id id}))
   :cljs
   (defmutation download-macaroon! [_props]
     (action [_env] true)
     (remote [env] (fm/returning env NodeMacaroonResponse))))

(defsc PeerResponse
  [_this _props]
  {:query         [:status]
   :initial-state {:status :initial}})

#?(:clj
   (pc/defmutation generate!
     [_env {::m.ln.nodes/keys [id]}]
     {::pc/params #{::m.ln.nodes/id}
      ::pc/output [:status]}
     (if-let [node (q.ln.nodes/read-record id)]
       (let [address (a.ln.nodes/generate! node)]
         {:status :ok :address address})
       {:status :not-found}))
   :cljs
   (defmutation generate! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (pc/defmutation fetch-accounts!
     [_env {::m.ln.nodes/keys [id]}]
     {::pc/params #{::m.ln.nodes/id}
      ::pc/output [:status]}
     (a.ln.accounts/do-fetch-accounts! id))
   :cljs
   (defmutation fetch-accounts! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (pc/defmutation fetch-channels!
     [_env {::m.ln.nodes/keys [id]}]
     {::pc/params #{::m.ln.nodes/id}
      ::pc/output [:status]}
     (a.ln.channels/fetch-channels! id)
     {:status :ok})
   :cljs
   (defmutation fetch-channels! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (pc/defmutation fetch-invoices!
     [_env {::m.ln.nodes/keys [id]}]
     {::pc/params #{::m.ln.nodes/id}
      ::pc/output [:status]}
     (comment id)
     {:status :ok})
   :cljs
   (defmutation fetch-invoices! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (pc/defmutation fetch-payments!
     [_env {::m.ln.nodes/keys [id]}]
     {::pc/params #{::m.ln.nodes/id}
      ::pc/output [:status]}
     (comment id)
     {:status :ok})
   :cljs
   (defmutation fetch-payments! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (pc/defmutation fetch-peers!
     [_env {::m.ln.nodes/keys [id] :as props}]
     {::pc/params #{::m.ln.nodes/id}
      ::pc/output [:status]}
     (if id
       (do
         (log/info :fetch-peers!/starting {:props props})
         (a.ln.peers/fetch-peers! id)
         {:status :ok})
       (do
         (log/error :fetch-peers!/missing-id {})
         (throw (ex-info "Missing id" {})))))

   :cljs
   (defmutation fetch-peers! [_props]
     (action [_env] true)
     (remote [env]
       (-> env
           (fm/returning PeerResponse)))))

#?(:clj
   (pc/defmutation fetch-transactions!
     [_env {::m.ln.nodes/keys [id]}]
     {::pc/params #{::m.ln.nodes/id}
      ::pc/output [:status]}
     (a.ln.transactions/fetch-transactions! id)
     {:status :ok})
   :cljs
   (defmutation fetch-transactions! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (pc/defmutation make-peer!
     [_env {node-id ::m.ln.nodes/id
            remote-node-id ::m.ln.remote-nodes/id}]
     {::pc/params #{::m.ln.nodes/id ::m.ln.remote-nodes/id}
      ::pc/output [:status]}
     (a.ln.peers/make-peer* node-id remote-node-id))
   :cljs
   (defmutation make-peer! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (pc/defmutation initialize!
     [_env {::m.ln.nodes/keys [id]}]
     {::pc/params #{::m.ln.nodes/id}
      ::pc/output [:status]}
     (if-let [node (q.ln.nodes/read-record id)]
       (do (a.ln.nodes/initialize! node)
           {:status "ok"})
       (do
         (log/error :initialize!/no-node {})
         {:status "fail"})))
   :cljs
   (defmutation initialize! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (pc/defmutation update-info!
     [_env {::m.ln.nodes/keys [id]}]
     {::pc/params #{::m.ln.nodes/id}
      ::pc/output [:status]}
     (if-let [node (q.ln.nodes/read-record id)]
       (let [ch (a.ln.nodes/update-info! node)]
         {:status (if (nil? ch) "fail" "pass")})
       (do (log/error :update-info!/no-node {})
           {:status "fail"})))
   :cljs
   (defmutation update-info! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (pc/defmutation update-transactions!
     [_env {::m.ln.nodes/keys [id]}]
     {::pc/params #{::m.ln.nodes/id}
      ::pc/output [:status]}
     (if-let [node (q.ln.nodes/read-record id)]
       (do
         (comment node)
         (throw (ex-info "not implemented" {}))
         {:status "ok"})
       (do (log/error :update-transactions!/no-node {})
           {:status "fail"})))
   :cljs
   (defmutation update-transactions! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (pc/defmutation unlock!
     [_env {::m.ln.nodes/keys [id]}]
     {::pc/params #{::m.ln.nodes/id}
      ::pc/output [:status]}
     (if-let [node (q.ln.nodes/read-record id)]
       (do (a.ln.nodes/unlock! node)
           {:status "ok"})
       (do (log/error :unlock!/no-node {})
           {:status "fail"})))
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
