(ns dinsro.core
  (:require [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]
            [datascript.core :as d]))

(enable-console-print!)

(println "Hello world!")
(def conn (d/create-conn {}))

(d/transact! conn
  [{:db/id -1
    :app/title "Hello, DataScript!"
    :app/count 0}])

(defmulti read om/dispatch)

(defmethod read :app/counter
  [{:keys [state query]} _ _]
  {:value (d/q '[:find [(pull ?e ?selector) ...]
                 :in $ ?selector
                 :where [?e :app/title]]
            (d/db state) query)})

(defmulti mutate om/dispatch)

(defmethod mutate 'app/increment
  [{:keys [state]} _ entity]
  {:value {:keys [:app/counter]}
   :action (fn [] (d/transact! state
                    [(update-in entity [:app/count] inc)]))})

(defui Counter
  ;; static om/IQuery
  ;; (query [this]
  ;;   [{:app/counter [:db/id :app/title :app/count]}])
  Object
  (render [this]
    (let [{:keys [app/title app/count] :as entity}
          (get-in (om/props this) [:app/counter 0])]
      (dom/div nil
        (dom/h2 nil title)
        (dom/span nil (str "Count: " count))
        (dom/button
          #js {:onClick
               (fn [e]
                 (om/transact! this
                   `[(app/increment ~entity)]))}
          "Click me!")))))

(def reconciler
  (om/reconciler
    {:state conn
     :parser (om/parser {:read read :mutate mutate})}))

(om/add-root! reconciler
  Counter (gdom/getElement "app"))
