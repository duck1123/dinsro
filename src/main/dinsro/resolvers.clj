(ns dinsro.resolvers
  (:require
   [com.wsscode.pathom.core :as p]
   [com.wsscode.pathom.connect :as pc :refer [defresolver]]
   [dinsro.model.users :as m.users]
   [dinsro.mutations.session :as mu.session]
   [taoensso.timbre :as log]))

(defresolver auth-resolver
  [_env _props]
  {::pc/output [:auth/id]}
  {:auth/id 1})

(defresolver current-user-resolver
  [env _props]
  {::pc/output
   [{:session/current-user
     [:user/username
      {:user/ref [::m.users/id]}
      :user/valid?]}]}
  (let [{:keys [request]}  env
        {:keys [session]}  request
        {:keys [identity]} session]
    {:session/current-user
     {:user/username identity
      :user/valid?   (boolean (seq identity))}}))

(defresolver index-explorer [env _]
  {::pc/input  #{:com.wsscode.pathom.viz.index-explorer/id}
   ::pc/output [:com.wsscode.pathom.viz.index-explorer/index]}
  (let [indexes (get env ::pc/indexes)]
    {:com.wsscode.pathom.viz.index-explorer/index
     (p/transduce-maps
      (remove (comp #{::pc/resolve ::pc/mutate} key))
      indexes)}))

(def resolvers
  [current-user-resolver
   mu.session/resolvers])
