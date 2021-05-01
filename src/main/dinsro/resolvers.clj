(ns dinsro.resolvers
  (:require
   [com.wsscode.pathom.core :as p]
   [com.wsscode.pathom.connect :as pc :refer [defresolver]]
   [dinsro.model.users :as m.users]
   [dinsro.mutations.accounts :as mu.accounts]
   [dinsro.mutations.categories :as mu.categories]
   [dinsro.mutations.currencies :as mu.currencies]
   [dinsro.mutations.rates :as mu.rates]
   [dinsro.mutations.rate-sources :as mu.rate-sources]
   [dinsro.mutations.session :as mu.session]
   [dinsro.mutations.transactions :as mu.transactions]
   [dinsro.mutations.users :as mu.users]
   [dinsro.resolvers.accounts :as r.accounts]
   [dinsro.resolvers.categories :as r.categories]
   [dinsro.resolvers.currencies :as r.currencies]
   [dinsro.resolvers.debug-menu :as r.debug-menu]
   [dinsro.resolvers.navlink :as r.navlink]
   [dinsro.resolvers.rates :as r.rates]
   [dinsro.resolvers.rate-sources :as r.rate-sources]
   [dinsro.resolvers.transactions :as r.transactions]
   [dinsro.resolvers.users :as r.users]
   [taoensso.timbre :as timbre]))

(defresolver auth-resolver
  [_env _props]
  {::pc/output [:auth/id]}
  {:auth/id 1})

(defresolver current-user-resolver
  [{{{:keys [identity]} :session} :request} _props]
  {::pc/output
   [{:session/current-user
     [:user/username
      {:user/ref [::m.users/username]}
      :user/valid?]}]}
  {:session/current-user {:user/username identity
                          :user/valid?   (boolean (seq identity))}})

(defresolver index-explorer [env _]
  {::pc/input  #{:com.wsscode.pathom.viz.index-explorer/id}
   ::pc/output [:com.wsscode.pathom.viz.index-explorer/index]}
  (let [indexes (get env ::pc/indexes)]
    {:com.wsscode.pathom.viz.index-explorer/index
     (p/transduce-maps
      (remove (comp #{::pc/resolve ::pc/mutate} key))
      indexes)}))

(def resolvers
  [auth-resolver
   current-user-resolver
   index-explorer
   mu.accounts/resolvers
   mu.categories/resolvers
   mu.currencies/resolvers
   mu.rates/resolvers
   mu.rate-sources/resolvers
   mu.session/resolvers
   mu.transactions/resolvers
   mu.users/resolvers
   r.accounts/resolvers
   r.categories/resolvers
   r.currencies/resolvers
   r.debug-menu/resolvers
   r.navlink/resolvers
   r.rates/resolvers
   r.rate-sources/resolvers
   r.transactions/resolvers
   r.users/resolvers])
