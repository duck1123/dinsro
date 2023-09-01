(ns dinsro.mocks.ui.navbars
  (:require
   [dinsro.joins.navlinks :as j.navlinks]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.specs :as ds]))

;; [[../../ui/navbars.cljc]]
;; [[../../../../notebooks/dinsro/notebooks/navbars_notebook.clj]]

(defn RouteTarget-data
  ([] (RouteTarget-data {}))
  ([_opts]
   {::m.navlinks/control (ds/gen-key ::m.navlinks/control)
    ::m.navlinks/id      (ds/gen-key ::m.navlinks/id)}))

(defn NavLink-data
  ([] (NavLink-data {}))
  ([_opts]
   {::m.navlinks/id      (ds/gen-key ::m.navlinks/id)
    ::m.navlinks/label (ds/gen-key ::m.navlinks/label)
    ::j.navlinks/path []}))
