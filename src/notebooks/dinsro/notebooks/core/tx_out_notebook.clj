^{:nextjournal.clerk/visibility {:code :hide}}
(ns dinsro.notebooks.core.tx-out-notebook
  (:require
   [dinsro.notebook-utils :as nu]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # Core TX-out

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility {:code :hide}}
(nu/display-file-links)

(def params
  {:dinsro.model.core.tx-out/asm                      nil,
   :dinsro.model.core.tx-out/hex                      nil,
   :dinsro.model.core.tx-out/type                     nil,
   :dinsro.model.core.tx-out/address                  nil,
   :dinsro.client.converters.rpc-transaction-output/n 1,
   :dinsro.client.converters.rpc-transaction-output/script-pub-key
   {:dinsro.client.converters.rpc-script-pub-key/addresses   [],
    :dinsro.client.converters.rpc-script-pub-key/asm
    "OP_RETURN aa21a9ede2f61c3f71d1defd3fa999dfa36953755c690689799962b48bebd836974e8cf9",
    :dinsro.client.converters.rpc-script-pub-key/hex
    "6a24aa21a9ede2f61c3f71d1defd3fa999dfa36953755c690689799962b48bebd836974e8cf9",
    :dinsro.client.converters.rpc-script-pub-key/script-type "nulldata"},
   :dinsro.client.converters.rpc-transaction-output/value
   {:dinsro.client.converters.currency-unit/value 0E-8},
   :dinsro.model.core.tx-out/transaction
   #uuid "01827983-7719-8804-8127-00620fb4c178"})
