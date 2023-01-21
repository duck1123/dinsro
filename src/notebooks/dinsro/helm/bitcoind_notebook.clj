^{:nextjournal.clerk/visibility {:code :hide}}
(ns dinsro.helm.bitcoind-notebook
  (:require
   [dinsro.helm.bitcoind :as h.bitcoind]
   [dinsro.notebook-utils :as nu]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # Bitcoind Helm generator

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility {:code :hide}}
(nu/display-file-links)

(def inputs {:name "notebooktest"
             ;; :network :mainnet
             :network :testnet
             ;; :network :regtest
             :rpc {:user "foo" :password "bar"}})

;; ## merge-defaults

^{::clerk/viewer clerk/code ::clerk/no-cache true}
(def merged-defaults (h.bitcoind/merge-defaults inputs))

^{::clerk/viewer clerk/code ::clerk/no-cache true}
(h.bitcoind/->bitcoin-conf merged-defaults)

;; ## ->values

;; This generates the helm values file for lnd

^{::clerk/viewer clerk/code ::clerk/no-cache true}
(h.bitcoind/->values merged-defaults)

;; ^{::clerk/viewer clerk/code ::clerk/no-cache true}
;; (h.bitcoind/->values-yaml merged-defaults)
