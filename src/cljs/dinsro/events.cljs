(ns dinsro.events
  (:require
   [ajax.core :as ajax]
   [dinsro.store :as st]
   [taoensso.timbre :as timbre]))

(defn fetch-request
  [path store on-success on-failure]
  {:uri             (st/path-for store path)
   :method          :get
   :response-format (ajax/json-response-format {:keywords? true})
   :on-success      on-success
   :on-failure      on-failure})

(defn fetch-request-auth
  [path store token on-success on-failure]
  {:uri             (st/path-for store path)
   :method          :get
   :headers         {"Authorization" (str "Token " token)}
   :response-format (ajax/json-response-format {:keywords? true})
   :on-success      on-success
   :on-failure      on-failure})

(defn delete-request
  [path store on-success on-failure]
  {:uri             (st/path-for store path)
   :method          :delete
   :format          (ajax/json-request-format)
   :response-format (ajax/json-response-format {:keywords? true})
   :on-success      on-success
   :on-failure      on-failure})

(defn delete-request-auth
  [path store token on-success on-failure]
  {:uri             (st/path-for store path)
   :method          :delete
   :headers {"Authorization" (str "Token " token)}
   :format          (ajax/json-request-format)
   :response-format (ajax/json-response-format {:keywords? true})
   :on-success      on-success
   :on-failure      on-failure})

(defn post-request
  [path store on-success on-failure data]
  {:method          :post
   :uri             (st/path-for store path)
   :params          data
   :format          (ajax/json-request-format)
   :response-format (ajax/json-response-format {:keywords? true})
   :on-success      on-success
   :on-failure      on-failure})

(defn post-request-auth
  [path store token on-success on-failure data]
  {:method          :post
   :uri             (st/path-for store path)
   :headers {"Authorization" (str "Token " token)}
   :params          data
   :format          (ajax/json-request-format)
   :response-format (ajax/json-response-format {:keywords? true})
   :on-success      on-success
   :on-failure      on-failure})
