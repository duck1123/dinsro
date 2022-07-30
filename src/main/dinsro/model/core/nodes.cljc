(ns dinsro.model.core.nodes
  (:refer-clojure :exclude [name])
  (:require
   [clojure.set :as set]
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [lambdaisland.glogc :as log]))

(s/def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::name string?)
(defattr name ::name :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::host string?)
(defattr host ::host :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::port int?)
(defattr port ::port :int
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::rpcuser string?)
(defattr rpcuser ::rpcuser :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::rpcpass string?)
(defattr rpcpass ::rpcpass :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::balance number?)
(defattr balance :wallet-info/balance :double
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::tx-count number?)
(defattr tx-count :wallet-info/tx-count :int
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::chain string?)
(defattr chain ::chain :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::pruned? boolean?)
(defattr pruned? ::pruned? :boolean
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::difficulty number?)
(defattr difficulty ::difficulty :double
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::size-on-disk number?)
(defattr size-on-disk ::size-on-disk :int
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::initial-block-download? boolean?)
(defattr initial-block-download? ::initial-block-download? :boolean
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::best-block-hash string?)
(defattr best-block-hash ::best-block-hash :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::verification-progress number?)
(defattr verification-progress ::verification-progress :int
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::warnings string?)
(defattr warnings ::warnings :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::headers number?)
(defattr headers ::headers :int
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::chainwork string?)
(defattr chainwork ::chainwork :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::block-count number?)
(defattr block-count ::block-count :int
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::required-params
  (s/keys :req [::name ::host ::port ::rpcuser ::rpcpass]))

(s/def ::blockchain-info
  (s/keys :req [::pruned? ::difficulty ::size-on-disk ::initial-block-download?
                ::best-block-hash ::verification-progress ::warnings ::headers
                ::chainwork ::chain ::block-count]))

(s/def ::params
  (s/keys :req [::name ::host ::port ::rpcuser ::rpcpass]
          :opt [::pruned? ::difficulty ::size-on-disk ::initial-block-download?
                ::best-block-hash ::verification-progress ::warnings ::headers
                ::chainwork ::chain ::block-count]))
(s/def ::item
  (s/keys :req [::id ::name ::host ::port ::rpcuser ::rpcpass]
          :opt [::pruned? ::difficulty ::size-on-disk ::initial-block-download?
                ::best-block-hash ::verification-progress ::warnings ::headers
                ::chainwork ::chain ::block-count]))
(s/def ::items (s/coll-of ::item))

(def rename-map
  {:pruned               ::pruned?
   :difficulty           ::difficulty
   :size_on_disk         ::size-on-disk
   :initialblockdownload ::initial-block-download?
   :bestblockhash        ::best-block-hash
   :verificationprogress ::verification-progress
   :warnings             ::warnings
   :headers              ::headers
   :softforks            ::softforks
   :chainwork            ::chainwork
   :chain                ::chain
   :blocks               ::block-count})

(>defn prepare-params
  [params]
  [any? => ::params]
  (let [params (set/rename-keys params rename-map)]
    (log/debug :params/prepared {:params params})
    params))

(def link-query [::id ::name])

(defn ident
  [id]
  {::id id})

(defn ident-item
  [{::keys [id]}]
  (ident id))

(defn idents
  [ids]
  (mapv ident ids))

;; #?(:clj
;;    (>defn get-client
;;      ([node]
;;       [::item => any?]
;;       (get-client node ""))
;;      ([{::keys [host port rpcuser rpcpass]} path]
;;       [::item string? => any?]
;;       (c.bitcoin/get-client
;;        {:http/url        (str "http://" host ":" port path)
;;         :http/basic-auth [rpcuser rpcpass]}))))

(def attributes
  [id name host port rpcuser rpcpass balance tx-count chain pruned? difficulty size-on-disk initial-block-download? best-block-hash verification-progress warnings headers chainwork block-count])
