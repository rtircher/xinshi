(ns xinshi.neo
  (:require [clojurewerkz.neocons.rest               :as nr]
            [clojurewerkz.neocons.rest.nodes         :as node]
            [clojurewerkz.neocons.rest.relationships :as rel]
            [clojurewerkz.neocons.rest.cypher        :as cypher])
  (:import [clojure.lang ExceptionInfo]))

(nr/connect! "http://localhost:7474/db/data/")
(def root (node/get 0))


(defrecord User [id first-name last-name email])
(defrecord Message [id from-uid to-uid sent-date text])

(defmacro ^:private ensure-found [body]
  `(try ~body
       (catch ExceptionInfo e#
         (when-not (= 404 (:status (:object  (.getData e#))))
           (throw e#)))))

(defn- make-user [neo-props]
  (let [data (:data neo-props)]
    (User. (:id neo-props)
           (:first_name data)
           (:last_name data)
           (:email data))))

(defn- make-message [neo-props]
  (let [props (into {} (map (fn [[k v]] [(keyword k) v]) neo-props))]
    (Message. (:id props)
              (:from-uid props)
              (:to-uid props)
              (:sent_date props)
              (:text props))))

(defn create-user! [first-name last-name email]
  (let [node (node/create {:first_name first-name
                           :last_name last-name
                           :email email})]
    (rel/create root node :user)
    (make-user node)))

(defn find-user [id]
  (ensure-found (make-user (node/get id))))

(defn create-node [props]
  (let [node (node/create props)]
    (assoc (:data node) :id (:id node))))

(defn add-message-to! [from-user to-user sent-date text]
  (let [message-node (create-node {:text text
                                   :sent_date sent-date})]
    (rel/create root message-node :message)
    (rel/create message-node to-user :to)
    (rel/create from-user message-node :from)
    (make-message (assoc message-node :from-uid (:id from-user) :to-uid (:id to-user)))))

(defn all-messages-for [from-user to-user]
  (map make-message
       (cypher/tquery
        "START user_from=node({fuid}), user_to=node({tuid}) 
         MATCH user_from-[:from]->message-[:to]->user_to
         RETURN id(message) AS id,
           id(user_from) AS from_uid,
           id(user_to) AS to_uid,
           message.sent_date AS sent_date,
           message.text AS text
         ORDER BY message.sent_date ASC"
           {:fuid (:id from-user)
            :tuid (:id to-user)})))
;; -> should to query return messages from and to both user to each other?
;; Look at morph into for result of cypher query
