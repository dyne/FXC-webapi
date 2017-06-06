(ns fxc-webapi.core-test
  (:require [cheshire.core :as cheshire]
            [midje.sweet :refer :all]
            [fxc-webapi.handler :refer :all]
            [ring.mock.request :as mock]))

(defn parse-body [body]
  (cheshire/parse-string (slurp body) true))

;; (facts "Compojure api tests"

;;   (fact "Test GET request to /hello?name={a-name} returns expected response"
;;     (let [response (app (-> (mock/request :get  "/api/plus?x=1&y=2")))
;;           body     (parse-body (:body response))]
;;       (:status response) => 200
;;       (:result body)    => 3)))
