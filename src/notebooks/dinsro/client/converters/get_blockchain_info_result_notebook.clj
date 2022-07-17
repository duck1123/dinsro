^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.client.converters.get-blockchain-info-result-notebook
  (:require
   [dinsro.client.bitcoin-s :as c.bitcoin-s]
   [dinsro.client.converters.get-blockchain-info-result :as c.converters.get-blockchain-info-result]
   [dinsro.client.scala :as cs]
   [dinsro.notebook-utils :as nu]
   [dinsro.specs :as ds]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk])
  (:import
   org.bitcoins.commons.jsonmodels.bitcoind.GetBlockChainInfoResultPostV19))

;; # Get Block Chain Info Result [link](https://bitcoin-s.org/api/org/bitcoins/commons/jsonmodels/bitcoind/GetBlockChainInfoResultPostV19.html)

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/display-file-links)

^{::clerk/visibility :hide ::clerk/viewer clerk/hide-result}
(def result
  (let [chain                   (c.bitcoin-s/regtest-network)
        blocks                  (int 1)
        headers                 (int 1)
        bestblockhash           (cs/double-sha256-digest-be "0a5d6b67612efcd122956820cb8ab6e660f14e4da6ea15c55f4fbee7b733d46f")
        difficulty              (cs/big-decimal 1)
        mediantime              (int 1)
        verification-progress   (cs/big-decimal 1)
        initial-block-download? true
        chainwork               "00000000"
        size-on-disk            1
        pruned?                 true
        pruneheight             (cs/option (int 1))
        softforks               nil
        warnings                ""]
    (GetBlockChainInfoResultPostV19.
     chain
     blocks
     headers
     bestblockhash
     difficulty
     mediantime
     verification-progress
     initial-block-download?
     chainwork
     size-on-disk
     pruned?
     pruneheight
     softforks
     warnings)))

result

^::clerk/no-cache
(nu/display2 (cs/->record result))

;; ## converted record

^{::clerk/viewer clerk/code ::clerk/no-cache true}
(and result (cs/->record result))

;; ## randomly generated record

^{::clerk/viewer clerk/code ::clerk/no-cache true}
(ds/gen-key ::c.converters.get-blockchain-info-result/record)
