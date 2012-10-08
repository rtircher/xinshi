(ns xinshi.response
  (:use [ring.util.response :only (response content-type status)])
  (:require [clj-json.core :as json]))

(defn html [data]
  (-> (response data)
      (content-type "text/html; charset=utf-8")))

(defn json
  ([data] (json data 200))
  ([data http-status]
     (-> (response (json/generate-string data))
         (content-type "application/json")
         (status http-status))))
