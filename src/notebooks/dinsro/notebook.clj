^{:nextjournal.clerk/visibility #{:code :hide}}
(ns dinsro.notebook
  (:require
   [dinsro.actions.site :as a.site]
   [dinsro.notebook-utils :as nu]
   [dinsro.site :as site]
   [dinsro.specs :as ds]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # Dinsro Notebooks

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility {:code :hide}}
(nu/display-file-links)

;; # Namespaces

^{::clerk/viewer clerk/table ::clerk/visibility {:code :hide}}
(nu/x2)

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility {:code :hide}}
(nu/x2)

(ds/gen-key ::site/site)

^{::clerk/no-cache true}
(ds/gen-key ::site/site-defaults)

(ds/gen-key :dinsro.site.devcards/devcards)

^{::clerk/viewer clerk/code}
(a.site/load-site-config)

^{::clerk/visibility {:result :hide}}
(comment

  (clerk/show! "src/notebooks/dinsro/index.md")
  (clerk/show! "src/notebooks/dinsro/notebook.clj")
  (clerk/show! "src/notebooks/dinsro/client/bitcoin_s_notebook.clj")

  nil)
