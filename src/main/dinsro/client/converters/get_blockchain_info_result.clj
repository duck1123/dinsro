(ns dinsro.client.converters.get-blockchain-info-result
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   dinsro.client.converters.get-block-result
   [dinsro.client.scala :as cs]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log])
  (:import
   org.bitcoins.commons.jsonmodels.bitcoind.GetBlockChainInfoResultPostV19))

(>def ::best-block-hash string?)
(>def ::blocks int?)
(>def ::chain string?)
(>def ::chainwork string?)
(>def ::difficulty number?)
(>def ::headers int?)
(>def ::initial-block-download? boolean?)
(>def ::median-time int?)
(>def ::product-element-names (s/coll-of string?))
(>def ::pruned? boolean?)
(>def ::pruned-height (s/or :nil nil? :number int?))
(>def ::size-on-disk number?)

;; Map[String, SoftforkPostV19]

(>def ::softforks any?)
(>def ::verification-progress number?)
(>def ::warnings string?)

(>def ::instance (ds/instance? GetBlockChainInfoResultPostV19))

(>def ::record
  (s/keys
   :req
   [::best-block-hash
    ::blocks
    ::chain
    ::chainwork
    ::difficulty
    ::headers
    ::initial-block-download?
    ::median-time
        ;; ::product-element-names
    ::size-on-disk
        ;; ::softforks
    ::verification-progress
    ::warnings]))

;; https://bitcoin-s.org/api/org/bitcoins/commons/jsonmodels/bitcoind/GetBlockChainInfoResultPostV19.html

(>defn GetBlockChainInfoResultPostV19->record
  [this]
  [::instance => ::record]
  (log/trace :GetBlockChainInfoResultPostV19->record/starting {:this this})
  (let [response {::best-block-hash         (some-> this .bestblockhash .hex)
                  ::blocks                  (.blocks this)
                  ::chain                   (some-> this .chain .name)
                  ::chainwork               (.chainwork this)
                  ::difficulty              (some-> this .difficulty .toLong)
                  ::headers                 (.headers this)
                  ::initial-block-download? (.initialblockdownload this)
                  ::median-time             (.mediantime this)
                  ;; :product-element-names   (.productElementNames this)
                  ::pruned                  (.pruned this)
                  ::prune-height            (some-> this .pruneheight cs/get-or-nil)
                  ::size-on-disk            (.size_on_disk this)
                  ;; :softforks               (.softforks this)
                  ::verification-progress   (some-> this .verificationprogress .toLong)
                  ::warnings                (.warnings this)}]
    (log/info :GetBlockChainInfoResultPostV19->record/response {;; :this this
                                                                :response response})
    response))

(extend-type GetBlockChainInfoResultPostV19
  cs/Recordable
  (->record [this] (GetBlockChainInfoResultPostV19->record this)))
