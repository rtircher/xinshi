(ns xinshi.core
  (:use compojure.core)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [xinshi.response :as response]))

(defroutes app-routes
  (GET "/" [] (response/json {:message "Hello from xinshi"}))
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))
