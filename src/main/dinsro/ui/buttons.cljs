(ns dinsro.ui.buttons
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.rad.control :as control]))

(defn get-control-value
  [report-instance id-key]
  (some->> report-instance comp/props
           :ui/controls
           (some (fn [c]
                   (let [{::control/keys [id]} c]
                     (when (= id id-key) c))))
           ::control/value))

(defn report-action
  "Create a report row button definition calling the mutation with this record's id param"
  [id-key mutation]
  (fn [this]
    (let [id (get-control-value this id-key)]
      (comp/transact! this [(mutation {id-key id})]))))

(defn report-action-button
  [button-label model-key mutation]
  {:type   :button
   :local? true
   :label  button-label
   :action (report-action model-key mutation)})

(defn fetch-button
  [model-key mutation]
  {:type   :button
   :label  "Fetch"
   :action (report-action model-key mutation)})

(defn row-action-button
  "Create a report row button definition calling the mutation with this record's id param"
  [button-label model-key mutation]
  {:label  button-label
   :action (fn [this p]
             (let [id    (get p model-key)
                   props {model-key id}]
               (comp/transact! this [(mutation props)])))})

(defn subrow-action-button
  "Create a report row button definition calling the mutation with this record's id and the parent record's id as params"
  [label model-key parent-model-key mutation]
  {:label  label
   :action (fn [report-instance p]
             (let [record-id (get p model-key)
                   parent-id (get-control-value report-instance parent-model-key)
                   props     {model-key record-id parent-model-key parent-id}]
               (comp/transact! report-instance [(mutation props)])))})

(defn sub-page-action-button
  [options]
  (let [{:keys [label mutation parent-key]} options]
    {:type  :button
     :label label
     :action
     (fn [report-instance]
       (let [parent-id (get-control-value report-instance parent-key)
             props     {parent-key parent-id}]
         (comp/transact! report-instance [(mutation props)])))}))
