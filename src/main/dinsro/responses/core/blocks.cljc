(ns dinsro.responses.core.blocks
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.mutations :as mu]))

(def model-key ::m.c.blocks/id)

(defsc DeleteResponse
  [_this _props]
  {:initial-state {::deleted-records []
                   ::mu/status       :initial
                   ::mu/errors       {}}
   :query         [{::deleted-records [model-key]}
                   {::mu/errors (comp/get-query mu/ErrorData)}
                   ::mu/status]})

(defsc FetchResponseResponse
  [_this _props]
  {:query [::m.c.blocks/id
           ::m.c.blocks/bits
           ::m.c.blocks/chainwork
           ::m.c.blocks/difficulty
           ::m.c.blocks/hash
           ::m.c.blocks/height
           ::m.c.blocks/merkle-root
           ::m.c.blocks/nonce
           ::m.c.blocks/size
           ::m.c.blocks/time
           ::m.c.blocks/version
           ::m.c.blocks/transaction-count
           ::m.c.blocks/median-time
           ::m.c.blocks/next-block
           ::m.c.blocks/previous-block
           ::m.c.blocks/weight
           ::m.c.blocks/version-hex
           ::m.c.blocks/stripped-size
           ::m.c.blocks/fetched?
           ::m.c.blocks/network]
   :ident ::m.c.blocks/id})

(defsc FetchResponse
  [_this _props]
  {:query [::mu/status
           {:item (comp/get-query FetchResponseResponse)}
           :message]})

(defsc SearchResponse
  [_ _]
  {:initial-state {::mu/status       :initial
                   :tx-id            nil
                   :node             nil
                   :tx               nil
                   ::m.c.blocks/item {}}
   :query         [::mu/status
                   :tx-id
                   :node
                   :tx
                   ::m.c.blocks/item]})
