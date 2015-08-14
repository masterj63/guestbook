(ns guestbook.routes.auth
  (:require [compojure.core :refer [defroutes GET POST]]
            [hiccup.form :refer
             [form-to label text-field password-field submit-button]]
            [guestbook.views.layout :a slayout]
            [guestbook.views.layout :as layout]
            [guestbook.models.db :as db]
            [noir.response :refer [redirect]]
            [noir.session :as session]
            [noir.validation :refer [rule errors? has-value? on-error]]
            [noir.util.crypt :as crypt]))

(defn format-error [[error]]
  [:p.error error])

(defn control [field name text]
  (list (on-error name format-error)
        (label name text)
        (field name)
        [:br]))

(defn login-page []
  (layout/common
    (form-to [:post "/login"]
             (control text-field :id "Nickname")
             (control password-field :pass "Password")
             (submit-button "Login"))))

(defn registration-page []
  (layout/common
    (form-to [:post "/register"]
             (control text-field :id "Nickname")
             (control password-field :pass1 "Password")
             (control password-field :pass2 "Retype Password")
             (submit-button "Create Account"))))

(defn handle-login [id pass]
  (let [user (db/get-user id)]
    (rule (has-value? id)
          [:id "Nickname required"])
    (rule (has-value? pass)
          [:pass "Password required"])
    (rule (and user (crypt/compare pass (:pass user)))
          [:pass "invalid password"])
    (if (errors? :id :pass)
      (login-page)
      (do (session/put! :user id)
          (redirect "/")))))


(defn handle-registration [id pass1 pass2]
  (rule (= pass1 pass2)
        [:pass "passwords did not match!"])
  (if (errors? :pass)
    (registration-page)
    (do (db/add-user-record {:id id :pass (crypt/encrypt pass1)})
        (redirect "/login"))))

(defroutes auth-routes
           (GET "/register" [_] (registration-page))
           (POST "/register" [id pass1 pass2]
             (handle-registration id pass1 pass2))

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