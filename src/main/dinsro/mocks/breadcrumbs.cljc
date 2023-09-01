(ns dinsro.mocks.breadcrumbs
  (:require
   [dinsro.joins.navlinks :as j.navlinks]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.specs :as ds]))

;; [[../model/navlinks.cljc]]
;; [[../../../notebooks/dinsro/notebooks/breadcrumbs_notebook.clj]]

(defn make-link
  ([]
   (make-link true))
  ([generate-path?]
   {o.navlinks/id         (ds/gen-key ::m.navlinks/id)
    ::j.navlinks/navigate {o.navlinks/id      "foo"
                           o.navlinks/control :target-control}
    ::j.navlinks/path     (if generate-path?
                            (map (fn [_] (make-link false))  (range 3))
                            [])
    o.navlinks/label      (ds/gen-key ::m.navlinks/label)
    o.navlinks/auth-link? false
    ::j.navlinks/target   nil
    :ui/router            {}}))

(defn Breadcrumbs-data
  []
  {:root/current-page (make-link)})
