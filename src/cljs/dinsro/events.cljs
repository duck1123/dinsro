(ns dinsro.events
  (:require
   [ajax.core :as ajax]
   [kee-frame.core :as kf]
   [taoensso.timbre :as timbre]))

(defn fetch-request
  [path on-success on-failure]
  {:uri             (kf/path-for path)
   :method          :get
   :response-format (ajax/json-response-format {:keywords? true})
   :on-success      on-success
   :on-failure      on-failure})

(defn fetch-request-auth
  [path token on-success on-failure]
  {:uri             (kf/path-for path)
   :method          :get
   :headers         {"Authorization" (str "Token " token)}
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

(defn delete-request-auth
  [path token on-success on-failure]
  {:uri             (kf/path-for path)
   :method          :delete
   :headers {"Authorization" (str "Token " token)}
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

(defn post-request-auth
  [path token on-success on-failure data]
  {:method          :post
   :uri             (kf/path-for path)
   :headers {"Authorization" (str "Token " token)}
   :params          data
   :format          (ajax/json-request-format)
   :response-format (ajax/json-response-format {:keywords? true})
   :on-success      on-success
   :on-failure      on-failure})
