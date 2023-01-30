(ns dinsro.actions.nostr.pubkeys-test
  (:require
   [clojure.test :refer [deftest use-fixtures]]
   ;; [dinsro.actions.authentication :as a.authentication]
   [dinsro.actions.nostr.pubkeys :as a.n.pubkeys]
   ;; [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   ;; [dinsro.model.users :as m.users]
   [dinsro.queries.nostr.pubkeys :as q.n.pubkeys]
   ;; [dinsro.specs :as ds]
   [dinsro.test-helpers :as th]
   [fulcro-spec.check :as _]
   [fulcro-spec.core :refer [assertions]]))

;; [[../../../../main/dinsro/actions/nostr/pubkeys.clj][Pubkey Actions]]

(def schemata [])

(use-fixtures :each (fn [f] (th/start-db f schemata)))

(deftest register-pubkey!
  (let [pubkey-hex "6fe701bde348f57e1068101830ad2015f32d3d51d0d685ff0f2812ee8635efec"]
    (assertions
     "Should initially be missing"
     (q.n.pubkeys/find-by-hex pubkey-hex) => nil

     "Should return an id"
     (a.n.pubkeys/register-pubkey! pubkey-hex) =check=> (_/is?* uuid?)

     "Should be created after registration"
     (q.n.pubkeys/find-by-hex pubkey-hex) =check=> (_/is?* uuid?))))

(deftest process-pubkey-meesage!
  (let [msg ""]
    (assertions
     (a.n.pubkeys/process-pubkey-message! msg) => nil)))
