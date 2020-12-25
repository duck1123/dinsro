(ns dinsro.resolvers
  (:require
   [com.wsscode.pathom.core :as p]
   [com.wsscode.pathom.connect :as pc :refer [defresolver]]
   [dinsro.session :as session]
   [dinsro.resolvers.accounts :as accounts]
   [dinsro.resolvers.categories :as categories]
   [dinsro.resolvers.currencies :as currencies]
   [dinsro.resolvers.debug-menu :as debug-menu]
   [dinsro.resolvers.navlink :as navlink]
   [dinsro.resolvers.rates :as rates]
   [dinsro.resolvers.rate-sources :as rate-sources]
   [dinsro.resolvers.transactions :as transactions]
   [dinsro.resolvers.users :as users]
   [taoensso.timbre :as timbre]))

(defresolver auth-resolver
  [_env _props]
  {::pc/output [:auth/id]}
  {:auth/id 1})

(defresolver current-user-resolver
  [{{{:keys [identity]} :session} :request} _props]
  {::pc/output [{:session/current-user [:user/id :user/valid?]}]}
  {:session/current-user {:user/id (do
                                     ;; FIXME: restore identity
                                     (comment identity)
                                     "bob@example.com")
                          :user/valid? true}})

(defresolver index-explorer [env _]
  {::pc/input  #{:com.wsscode.pathom.viz.index-explorer/id}
   ::pc/output [:com.wsscode.pathom.viz.index-explorer/index]}
  (let [indexes (get env ::pc/indexes)]
    {:com.wsscode.pathom.viz.index-explorer/index
     (p/transduce-maps
      (remove (comp #{::pc/resolve ::pc/mutate} key))
      indexes)}))

(def resolvers
  [accounts/resolvers
   auth-resolver
   categories/resolvers
   currencies/resolvers
   current-user-resolver
   debug-menu/resolvers
   index-explorer
   session/resolvers
   navlink/resolvers
   rates/resolvers
   rate-sources/resolvers
   transactions/resolvers
   users/resolvers])
