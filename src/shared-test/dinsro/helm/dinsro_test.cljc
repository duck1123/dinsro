^{:nextjournal.clerk/visibility #{:hide-ns}
  :nextjournal.clerk/toc        true}
(ns dinsro.helm.dinsro-test)

(def site-config
  {:baseUrl             "dinsro.localhost"
   :repo                "duck1123"
   :version             "latest"
   :projectId           "p-vhkqf"
   :devcards
   {:enabled     true
    :inheritHost true
    :host        "devcards.dinsro.localhost"}
   :useDocs             true
   :useNotebook         true
   :notebookInheritHost false
   :notebookHost        "notebook.dinsro.localhost"
   :portalHost          "portal.dinsro.localhost"
   :localDevtools       true
   :useCards            true
   :seedDatabase        false
   :useGuardrails       true
   :useLinting          true
   :useNrepl            false
   :usePersistence      false
   :usePortal           true
   :useProduction       false
   :useTests            false})
