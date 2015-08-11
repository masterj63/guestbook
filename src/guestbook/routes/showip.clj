(ns guestbook.routes.showip
  (:require [compojure.core :refer [defroutes
                                    GET]]))

(defroutes ip-routes
           (GET "/ip" {ip :remote-addr} (format "You ip is `%s'." ip)))