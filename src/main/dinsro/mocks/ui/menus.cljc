(ns dinsro.mocks.ui.menus
  (:require
   [dinsro.mocks.ui.navbars :as mo.u.navbars]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.specs :as ds]))

;; [[../../ui/menus.cljc]]
;; [[../../../../notebooks/dinsro/notebooks/menus_notebook.clj]]

(defn NavMenu-data
  ([] (NavMenu-data {}))
  ([opts]
   {:id                  nil
    ::m.navbars/id       (ds/gen-key ::m.navbars/id)
    ::m.navbars/children (ds/make-rows opts mo.u.navbars/NavLink-data)}))

(defn VerticalMenu-data
  ([] (VerticalMenu-data {}))
  ([opts]
   {:id                  nil
    ::m.navbars/id       (ds/gen-key ::m.navbars/id)
    ::m.navbars/children (ds/make-rows opts mo.u.navbars/NavLink-data)}))
