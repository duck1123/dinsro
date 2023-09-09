(ns dinsro.mocks.ui.navbars
  (:require
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [dinsro.joins.navlinks :as j.navlinks]
   [dinsro.machines :as machines]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.mutations.navbars :as mu.navbars]
   [dinsro.specs :as ds]))

;; [[../../ui/navbars.cljc]]
;; [[../../../../notebooks/dinsro/notebooks/navbars_notebook.clj]]

(def unauth-links
  [{::m.navlinks/id         :foo
    ::m.navlinks/label      (ds/gen-key ::m.navlinks/label)
    ::m.navlinks/auth-link? false
    ::m.navlinks/target     nil
    :ui/router              {}}])

(def menu-links
  [])

(def nav-state
  {::uism/asm-id           ::mu.navbars/navbarsm
   ::uism/state-machine-id machines/hideable
   ::uism/active-state     :state/hidden
   ::uism/ident->actor     {:actor/navbar {}}
   ::uism/actor->ident     {:actor/navbar {}}})

(defn RouteTarget-data
  ([] (RouteTarget-data {}))
  ([_opts]
   {::m.navlinks/control (ds/gen-key ::m.navlinks/control)
    ::m.navlinks/id      (ds/gen-key ::m.navlinks/id)}))

(defn NavLink-data
  ([] (NavLink-data {}))
  ([_opts]
   {::m.navlinks/id    (ds/gen-key ::m.navlinks/id)
    ::m.navlinks/label (ds/gen-key ::m.navlinks/label)
    ::j.navlinks/path  []}))
