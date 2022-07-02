^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.client.converters.get-block-result-notebook
  (:require
   [dinsro.client.converters.get-block-result :as c.converters.get-block-result]
   [dinsro.client.scala :as cs]
   [dinsro.notebook-utils :as nu]
   [dinsro.specs :as ds]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk])
  (:import
   org.bitcoins.commons.jsonmodels.bitcoind.GetBlockResult))

;; # Get Block Result [link](https://bitcoin-s.org/api/org/bitcoins/commons/jsonmodels/bitcoind/GetBlockResult.html#%3Cinit%3E(hash:org.bitcoins.crypto.DoubleSha256DigestBE,confirmations:Int,strippedsize:Int,size:Int,weight:Int,height:Int,version:Int,versionHex:org.bitcoins.core.number.Int32,merkleroot:org.bitcoins.crypto.DoubleSha256DigestBE,tx:Vector[org.bitcoins.crypto.DoubleSha256DigestBE],time:org.bitcoins.core.number.UInt32,mediantime:org.bitcoins.core.number.UInt32,nonce:org.bitcoins.core.number.UInt32,bits:org.bitcoins.core.number.UInt32,difficulty:BigDecimal,chainwork:String,previousblockhash:Option[org.bitcoins.crypto.DoubleSha256DigestBE],nextblockhash:Option[org.bitcoins.crypto.DoubleSha256DigestBE]):org.bitcoins.commons.jsonmodels.bitcoind.GetBlockResult)

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/display-file-links)

(def get-block-result
  (let [hash                (cs/double-sha256-digest-be "0a5d6b67612efcd122956820cb8ab6e660f14e4da6ea15c55f4fbee7b733d46f")
        confirmations       (int 0)
        stripped-size       (int 0)
        size                (int 0)
        weight              (int 0)
        height              (int 0)
        version             (int 2)
        version-hex         (cs/int32 0)
        merkle-root         (cs/double-sha256-digest-be "0a5d6b67612efcd122956820cb8ab6e660f14e4da6ea15c55f4fbee7b733d46f")
        tx                  nil
        time                nil
        median-time         (cs/uint32 0)
        nonce               (cs/uint32 0)
        bits                (cs/uint32 0)
        difficulty          (cs/big-decimal 1)
        chainwork           "000000000"
        previous-block-hash nil
        next-block-hash     nil]
    (GetBlockResult.
     hash confirmations stripped-size size weight height version
     version-hex merkle-root tx time median-time nonce bits difficulty
     chainwork previous-block-hash next-block-hash)))

get-block-result

;; converted record

^{::clerk/viewer clerk/code ::clerk/no-cache true}
(cs/->record get-block-result)

;; randomly generated record

^{::clerk/viewer clerk/code ::clerk/no-cache true}
(ds/gen-key ::c.converters.get-block-result/record)
