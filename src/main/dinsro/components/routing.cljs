(ns dinsro.components.routing
  (:require
   [com.fulcrologic.fulcro.mutations :as m]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.authorization :as auth]
   [com.fulcrologic.rad.routing :as routing]
   [com.fulcrologic.rad.routing.html5-history :as hist5]
   [dinsro.ui.admin :as u.admin]
   [dinsro.ui.admin.nostr :as u.a.nostr]
   [dinsro.ui.admin.nostr.dashboard :as u.a.n.dashboard]
   [dinsro.ui.admin.users :as u.a.users]
   [dinsro.ui.core.networks :as u.c.networks]
   [dinsro.ui.core.networks.addresses :as u.c.n.addresses]
   [dinsro.ui.home :as u.home]
   [lambdaisland.glogc :as log]))

(defn restore-route-ensuring-leaf!
  "Attempt to restore the route given in the URL. If that fails, simply route to the default given (a class and map).
   WARNING: This should not be called until the HTML5 history is installed in your app.
   (Based on `hist5/restore-route!` modified to check for Partial Routes and routing to the correct leaf target.)

   NOTE: Fulcro dyn. routing requires that you always route to a leaf target, i.e. not just to a router somewhere in
   the middle of your UI tree with some unrouted, descendant routers - otherwise weird stuff may happen."
  [app]
  (let [{:keys [route params]} (hist5/url->route)
        requested-target       (dr/resolve-target app route)
        resolved-target        (condp = requested-target
                                 nil                   u.home/IndexPage
                                 u.admin/IndexPage     u.a.users/IndexPage
                                 u.a.nostr/IndexPage   u.a.n.dashboard/IndexPage
                                 u.c.networks/ShowPage u.c.n.addresses/SubPage
                                 requested-target)]
    (log/info :restore-route-ensuring-leaf!/routing
      {:resolved-target  resolved-target
       :requested-target requested-target
       :params           params})
    (routing/route-to! app resolved-target (or params {}))))

(m/defmutation fix-route
  "Mutation. Called after auth startup. Looks at the session. If the user is not logged in, it triggers authentication"
  [_]
  (action [{:keys [app] :as env}]
    (log/info :fix-route/starting {:env env})
    (let [logged-in (auth/verified-authorities app)]
      (if (empty? logged-in)
        (hist5/restore-route! app u.home/IndexPage {})
        (hist5/restore-route! app u.home/IndexPage {})))))
