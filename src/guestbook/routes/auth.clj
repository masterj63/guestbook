(ns guestbook.routes.auth
  (:require [compojure.core :refer [defroutes GET POST]]
            [guestbook.views.layout :a slayout]
            [hiccup.form :refer
             [form-to label text-field password-field submit-button]]
            [guestbook.views.layout :as layout]
            [noir.response :refer [redirect]]
            [noir.session :as session]))

(defn control [field name text]
  (list (label name text)
        (field name)
        [:br]))

(defn login-page [& [error]]
  (layout/common
    (if error [:div.error "Login error:" error])
    (form-to [:post "/login"]
             (control text-field :id "Nickname")
             (control password-field :pass "Password")
             (submit-button "Login"))))

(defn handle-login [id pass]
  (cond
    (empty? id) (login-page "Nickname is required")
    (empty? pass) (login-page "Password is required")
    (and (= "foo" id)
         (= "bar" pass)) (do
                           (session/put! :user id)
                           (redirect "/"))
    :else (login-page "Authentication failed")))

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
               (registration-page)))

           (GET "/login" [] (login-page))
           (POST "/login" [id pass]
             (handle-login id pass))

           (GET "/logout" []
             (layout/common
               (form-to [:post "/logout"]
                        (submit-button "Logout"))))
           (POST "/logout" []
             (session/clear!)
             (redirect "/")))