(ns dinsro.responses.core.wallets
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.core.words :as m.c.words]
   [dinsro.mutations :as mu]))

(defsc CreationResponse
  [_this _props]
  {:query [:mu/status ::m.c.wallets/id]})

(defsc RollWord
  [_this _props]
  {:query [::m.c.words/word ::m.c.words/position ::m.c.words/id]
   :ident ::m.c.words/id})

(defsc RollWallet
  [_this _props]
  {:query [::m.c.wallets/id
           ::m.c.wallets/key
           {::m.c.wallets/words (comp/get-query RollWord)}]
   :ident ::m.c.wallets/id})

(defsc RollResponse
  [_this _props]
  {:query [::mu/status {::m.c.wallets/item (comp/get-query RollWallet)}]})

(defsc DeriveResponse
  [_this _props]
  {:query [::mu/status]})

(defsc CalculateAddressesResponse
  [_this _props]
  {:query [::mu/status]})
