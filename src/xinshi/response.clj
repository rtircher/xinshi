(ns xinshi.response
  (:use [ring.util.response :only (response content-type)])
  (:require [clj-json.core :as json]))

(defn html [data]
  (-> (response data)
      (content-type "text/html; charset=utf-8")))

(defn json [data]
  (-> (response (json/generate-string data))
      (content-type "application/json")))
