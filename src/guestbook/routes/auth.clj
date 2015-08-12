(ns guestbook.routes.auth
  (:require [compojure.core :refer [defroutes GET POST]]
            [guestbook.views.layout :a slayout]
            [hiccup.form :refer
             [form-to label text-field password-field submit-button]]
            [guestbook.views.layout :as layout]
            [noir.response :refer [redirect]]))

(defn control [field name text]
  (list (label name text)
        (field name)
        [:br]))

(defn registration-page []
  (layout/common
    (form-to [:post "/register"]
             (control text-field :id "Nickname")
             (control password-field :pass1 "Password")
             (control password-field :pass2 "Retype Password")
             (submit-button "Create Account"))))

(defroutes auth-routes
           (GET "/register" [_] (registration-page))
           (POST "/register" [id pass1 pass2]
             (println id pass1 pass2)
             (if (= pass1 pass2)
               (redirect "/")
               (registration-page))))