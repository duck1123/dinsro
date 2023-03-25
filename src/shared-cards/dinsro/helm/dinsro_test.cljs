^{:nextjournal.clerk/visibility {:code :hide}
  :nextjournal.clerk/toc        true}
(ns dinsro.helm.dinsro-test
  (:require
   [dinsro.helm.dinsro :as h.dinsro]
   [nextjournal.devcards :as dc]
   [nextjournal.viewer :as viewer :refer [inspect]]))

(def site-config
  {:baseUrl        "dinsro.localtest.me"
   :repo           "duck1123"
   :version        "latest"
   :projectId      "p-vhkqf"
   :devcards       {:enabled     true
                    :inheritHost true
                    :host        "devcards.dinsro.localtest.me"}
   :useDocs        true
   :portalHost     "portal.dinsro.localtest.me"
   :localDevtools  true
   :useCards       true
   :seedDatabase   false
   :useGuardrails  true
   :useLinting     true
   :useNrepl       false
   :usePersistence false
   :usePortal      true
   :useProduction  false
   :useTests       false})

(dc/defcard site-config-card [] [inspect site-config])

(dc/defcard merge-defaults-card
  "```clojure
(merge-defaults site-config)
````
`"
  []
  [viewer/inspect (h.dinsro/merge-defaults site-config)])

(dc/defcard bar-card
  "bar"
  []
  [:pre [:code (str {:a :b})]])

(dc/defcard ->dinsro-config-card
  "Dinsro Config"
  (let [response (h.dinsro/->dinsro-config site-config)]
    [viewer/inspect response
     #_(viewer/view-as :code)]))
