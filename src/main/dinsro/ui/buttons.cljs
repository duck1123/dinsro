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
  [id-key mutation]
  (fn [this]
    (let [id (get-control-value this id-key)]
      (comp/transact! this [(mutation {id-key id})]))))

(defn fetch-button
  [id-key mutation]
  {:type   :button
   :label  "Fetch"
   :action (report-action id-key mutation)})

(defn row-action-button
  [label id-key mutation]
  {:label  label
   :action (fn [report-instance p]
             (let [id    (get p id-key)
                   props {id-key id}]
               (comp/transact! report-instance [(mutation props)])))})

(defn subrow-action-button
  [label id-key parent-key mutation]
  {:label  label
   :action (fn [report-instance p]
             (let [id        (get p id-key)
                   parent-id (get-control-value report-instance parent-key)
                   props     {id-key id parent-key parent-id}]
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
