(ns guestbook.handler
  (:use compojure.core
        ring.middleware.resource
        ring.middleware.file-info
        hiccup.middleware
        guestbook.routes.home)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [guestbook.models.db :as db]
            [guestbook.routes.auth :refer [auth-routes]]
            [noir.session :as session]
            [ring.middleware.session.memory :refer [memory-store]]
            [noir.validation :refer [wrap-noir-validation]]))


(defn init []
  (println "guestbook is starting")
  (when-not (.exists (java.io.File. "./db.sq3"))
    (db/create-guestbook-table)
    (db/create-user-table)))

(defn destroy []
  (println "guestbook is shutting down"))

(defroutes app-routes
           (route/resources "/")
           (route/not-found "Not Found"))

(def app
  (-> (routes auth-routes home-routes app-routes)
      handler/site
      wrap-base-url
      (session/wrap-noir-session
        {:session (memory-store)})
      (wrap-noir-validation)))


