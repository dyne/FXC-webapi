;; FXC web API - Simple Secret Sharing

;; Copyright (C) 2017 Dyne.org foundation

;; Sourcecode designed, written and maintained by
;; Denis Roio <jaromil@dyne.org>

;; This program is free software: you can redistribute it and/or modify
;; it under the terms of the GNU Affero General Public License as published by
;; the Free Software Foundation, either version 3 of the License, or
;; (at your option) any later version.

;; This program is distributed in the hope that it will be useful,
;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;; GNU Affero General Public License for more details.

;; You should have received a copy of the GNU Affero General Public License
;; along with this program.  If not, see <http://www.gnu.org/licenses/>.

(ns fxc-webapi.handler
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [schema.core :as s]
            [ring.swagger.json-schema :as rjs]
            [fxc.core :as fxc]
            [ring.middleware.defaults :refer
             [wrap-defaults site-defaults]]
            [ring.middleware.session :refer :all]
            [markdown.core :as md]
            [fxc-webapi.config :refer :all]))

;; https://github.com/metosin/ring-swagger

;; sanitize configuration or returns nil if not found
(defn get-config [obj]
  (if (contains? obj :config)
    (let [mc (merge config-default (:config obj))]
      (merge mc {:total  (Integer. (:total mc))
                 :quorum (Integer. (:quorum mc))}))
    nil))

;; generic wrapper to make conf structure optional on fxc calls
(defn run-fxc [func obj schema]
  (if-let [conf (get-config obj)]
    {:data (func conf (:data obj))
     :config conf}
    {:data (func config-default (:data obj))
     :config config-default}))


(def config-scheme
  {
   (s/optional-key :total)    (rjs/field s/Int {:example 5})
   (s/optional-key :quorum)   (rjs/field s/Int {:example 3})
   (s/optional-key :alphabet)
   (rjs/field
    s/Str {:example "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"})
   (s/optional-key :salt)
   (rjs/field s/Str {:example "gvXpBGp32DRIsPy1m1G3VlWHAF5nsi0auYnMIJQ0odZRKAGC"})
   (s/optional-key :prime) (rjs/field s/Str {:example 'prime4096})
   (s/optional-key :max) (rjs/field s/Int {:example 256})
   (s/optional-key :pgp-pub-keyring) s/Str
   (s/optional-key :pgp-sec-keyring) s/Str
   })

(s/defschema Config
  {(s/required-key :config) config-scheme})

(s/defschema Secret
  {(s/required-key :data)
   (rjs/field s/Str {:example "La gatta sul tetto che scotta"})
   (s/optional-key :config) config-scheme
   })


(s/defschema Shares
  {(s/required-key :data)
   (rjs/field
    [s/Str]
    {:example
     ["3NQFX9V46VNDB2K394ZMUM8MLZRWNCZQKXN5WT42R57L6KBD3Z7VRL5B3864MNX9U6725R6EVHG"
      "KNGF57RRP369H5DX4KKWUQ9KZ8W99TL2PR3G2WFGE7D3X6MBD736WV3LFXZ85M3V8H9D9ZZE5KSL"
      "XLVF45ELQ7MGSZQEW2GP4C234G4WPQTKWKMM9MEBL79RLVV2SV5LZ569KFDLDNRWX6F932X74W6HQ"
      "REWFRXX9W3EECNR5QEMXXUWNR7RXNZBQER43PK5S84E2ZLR7T4KZDRZV4B569QZP5NSRGQ4D83ESK"
      "79PFVMVX4RK8FDRQ9DQGVCR3MV3V7DH7KDM6GZQAG3RM7P26H5MQ35L3GSR4X8N8KQF84KRKR59T3"]})
   (s/optional-key :config) config-scheme})

(def rest-api
  (api
   {:swagger
    {:ui "/"
     :spec "/swagger.json"

     :data {:info {:version "0.1.0"
                   :title "FXC-webapi"
                   :description "FXC web API for simple secret sharing"}}}}

   (context "/" []
            :tags ["static"]
            (GET "/readme" request
                 {:headers {"Content-Type"
                            "text/html; charset=utf-8"}
                  :body (md/md-to-html-string
                         (slurp "README.md"))}))


   (context "/api/v1" []
            :tags ["FXC1"]

            (POST "/share" []
                  :return Shares
                  :body [secret Secret]
                  :summary "Split a secret into shares"
                  :description "

Takes a JSON structure made of a `secret` string and a `config`
structure of optional fields (defaults are applied when missing) where
most relevant settings are `total` and `quorum`.

It executes the FXC secret sharing on the `secret` and returns a
`shares` array of strings plus the complete `config` used to split the
secret into `total` shares, for which a `quorum` quantity of shares is
enough to retrieve the original secret.

"
                  (ok (run-fxc fxc/encode secret Secret)))



            (POST "/combine" []
                  :return Secret
                  :body [shares Shares]
                  :summary "Combine shares into a secret"
                  (ok (run-fxc fxc/decode shares Shares)))


            (POST "/generate" []
                  :return Secret
                  :body [config Config]
                  :summary "Generate a random string of defined length"
                  (ok (if-let [conf (get-config config)]
                        {:data (fxc/generate conf (:max conf))
                         :config conf})))
            )))

(def rest-api-defaults
  "A default configuration for a browser-accessible website that's accessed
  securely over HTTPS."
  (-> site-defaults
      (assoc-in [:cookies] false)
      (assoc-in [:security :anti-forgery] false)
      (assoc-in [:security :ssl-redirect] false)
      (assoc-in [:security :hsts] true)))

(def app
  (wrap-defaults rest-api rest-api-defaults))
