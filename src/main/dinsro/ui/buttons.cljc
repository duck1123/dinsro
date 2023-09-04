(ns dinsro.ui.buttons
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.guardrails.core :refer [>defn => ?]]
   [com.fulcrologic.rad.control :as control]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.semantic-ui.elements.button.ui-button :refer [ui-button]]
   [lambdaisland.glogc :as log]))

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

(defn form-create-button
  [label Form]
  {:label label
   :type :button
   :action #(form/create! % Form)})

(defn form-edit-button
  [this model-key label Form]
  (ui-button
   {:onClick (fn [_] (->> this comp/props model-key (form/edit! this Form)))}
   label))

(defn form-edit-icon-button
  [this model-key icon Form]
  (ui-button {:icon    icon
              :onClick (fn [_]
                         (let [props (comp/props this)
                               id    (model-key props)]
                           (form/edit! this Form id)))}))

(defn row-action-button
  "Create a report row button definition calling the mutation with this record's id param"
  [button-label model-key mutation]
  {:label  button-label
   :action (fn [this p]
             (let [id    (get p model-key)
                   props {model-key id}]
               (comp/transact! this [(mutation props)])))})

(>defn form-action-button
  "Create a form button definition calling the mutation with this record's keys"
  [button-label mutation request-keys]
  [string? any? set? => any?]
  {:type   :button
   :local? true
   :label  button-label
   :action (fn [this]
             (let [props         (comp/props this)
                   request-props (select-keys props (seq request-keys))]
               (log/info :form-action-button/clicked {:request-props request-props})
               (comp/transact! this [(mutation request-props)])))})

(defn subrow-action-button
  "Create a report row button definition calling the mutation with this record's id and the parent record's id as params"
  [label model-key parent-model-key mutation]
  {:label  label
   :action (fn [report-instance p]
             (let [record-id (get p model-key)
                   parent-id (get-control-value report-instance parent-model-key)
                   props     {model-key record-id parent-model-key parent-id}]
               (log/debug :subrow-action-button/clicked
                 {:record-id        record-id
                  :model-key        model-key
                  :parent-id        parent-id
                  :parent-model-key parent-model-key
                  :props            props})
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

(defn refresh-button
  [this]
  (ui-button {:icon    "refresh"
              :onClick (fn [_] (control/run! this))}))

(>defn action-button
  "Create a button that fires an action"
  [mutation label model-key this]
  [any? string? keyword? any? => any?]
  (ui-button
   {:onClick
    (fn [_]
      (let [props (comp/props this)
            id (model-key props)]
        (comp/transact! this [`(~mutation {~model-key ~id})])))}
   label))

(defn create-button
  [this Form]
  (ui-button
   {:onClick
    (fn [_]
      (log/info :create-button/clicked {:this this})
      (form/create! this Form))}
   "create"))

(defn delete-button
  [mutation model-key this]
  (ui-button {:icon "delete"
              :onClick
              (fn [p]
                (log/info :delete-button/clicked {:p p :this this})
                (let [props (comp/props this)
                      id (model-key props)]
                  (comp/transact! this [`(~mutation {~model-key ~id})])))}))
