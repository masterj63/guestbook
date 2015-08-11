(ns guestbook.handler
  (:use compojure.core
        ring.middleware.resource
        ring.middleware.file-info
        hiccup.middleware)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [guestbook.models.db :as db]
            [guestbook.routes.home]
            [guestbook.routes.showip]))

(defn init []
  (println "guestbook is starting")
  (if-not (.exists (java.io.File. "./db.sq3"))
    (db/create-guestbook-table)))

(defn destroy []
  (println "guestbook is shutting down"))

(defroutes app-routes
           (route/resources "/")
           (route/not-found "Not Found"))

(def app
  (-> (routes
        guestbook.routes.home/home-routes
        guestbook.routes.showip/ip-routes
        app-routes)
      (handler/site)
      (wrap-base-url)))


