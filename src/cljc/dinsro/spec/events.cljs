(ns dinsro.events
  (:require [ajax.core :as ajax]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.specs :as ds]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]
            [taoensso.timbre :as timbre]))

(defn fetch-request
  [path on-success on-failure]
  {:uri             (kf/path-for path)
   :method          :get
   :response-format (ajax/json-response-format {:keywords? true})
   :on-success      on-success
   :on-failure      on-failure})

(defn delete-request
  [path on-success on-failure]
  {:uri             (kf/path-for path)
   :method          :delete
   :format          (ajax/json-request-format)
   :response-format (ajax/json-response-format {:keywords? true})
   :on-success      on-success
   :on-failure      on-failure})

(defn post-request
  [path on-success on-failure data]
  {:method          :post
   :uri             (kf/path-for path)
   :params          data
   :format          (ajax/json-request-format)
   :response-format (ajax/json-response-format {:keywords? true})
   :on-success      on-success
   :on-failure      on-failure})
