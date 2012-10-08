(ns xinshi.neo
  (:require [clojurewerkz.neocons.rest :as nr]
            [clojurewerkz.neocons.rest.nodes :as node]
            [clojurewerkz.neocons.rest.relationships :as rel]
            [clojurewerkz.neocons.rest.cypher :as cypher]))

(nr/connect! "http://localhost:7474/db/data/")
(def root (node/get 0))


(defrecord User [id first-name last-name email])
(defrecord Message [id from-uid to-uid sent-date text])

(defn- make-user [neo-props]
  (let [data (:data neo-props)]
    (User. (:id neo-props)
           (:first-name data)
           (:last-name data)
           (:email data))))

(defn- make-message [neo-props]
  (let [data (:data neo-props)]
    (Message. (:id neo-props)
              ""
              ""
              (:sent-date data)
              (:text data))))

(defn create-user! [first-name last-name email]
  (let [node (node/create {:first-name first-name
                           :last-name last-name
                           :email email})]
    (rel/create root node :user)
    (make-user node)))

(defn find-user [id]
  (make-user (node/get id)))


(defn add-message-to! [user from-user sent-date text]
  (let [message-node (node/create {:text text
                                   :sent-date sent-date})]
    (rel/create root message-node :message)
    (rel/create message-node user :to)
    (rel/create message-node from-user :from)
    (make-message message-node)))

(defn- all-messages-for [user-from user-to]
  (cypher/tquery
   "START user_from=node({fid})
    MATCH user_from-[:from]->m-[:to]->user_to
    WHERE usert_to.id={tid}
    RETURN m
    ORDER BY m.sent-date"
   {:fid (:id user-from)
    :tid (:id user-to)}))
