(ns xinshi.handler
  (:use compojure.core
        xinshi.neo
        [xinshi.response :only [json]])
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            xinshi.middlewares.logger))

(defroutes users-routes
  (GET "/:id" [id]
    (json (find-user (read-string id))))

  (POST "/" {params :params}
    (let [{:keys [first-name last-name email]} params]
      (if (not (and first-name last-name email))
        (json {:error-message "first-name, last-name, and email prameters are required"} 400)
        (let [user (create-user! first-name last-name email)]
          (json user))))))


(defn- validate-add-message [sent-date text from-user to-user]
  ;;(let [error-message ])
  )

(defroutes messages-routes
  (POST "/" {params :params}
    (let [{:keys [sent-date text from-uid to-uid]} params
          to-user   (and to-uid   (find-user (read-string to-uid)))
          from-user (and from-uid (find-user (read-string from-uid)))]
      (if-let [error-message (validate-add-message sent-date text from-user to-user)]
        (json {:error-message error-message} 400)
        (json (add-message-to! from-user to-user sent-date text))))))


(defroutes api-routes
  (context "/users"    [] users-routes)
  (context "/messages" [] messages-routes)
  (route/not-found "Not Found"))

(def app
  (-> (handler/api api-routes)
      (xinshi.middlewares.logger/wrap-request-logging)))
