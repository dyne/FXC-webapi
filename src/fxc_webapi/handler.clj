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
            [fxc-webapi.config :refer :all]))

;; https://github.com/metosin/ring-swagger

(defn get-config
  [obj] {:pre [(contains? obj :config)]}
  (let [mc (merge config-default (:config obj))]
    (merge mc {:total  (Integer. (:total mc))
               :quorum (Integer. (:quorum mc))})))


(def config-scheme
  {
   (s/optional-key :total)    (rjs/field s/Int {:example 5})
   (s/optional-key :quorum)   (rjs/field s/Int {:example 3})
   (s/optional-key :alphabet)
   (rjs/field s/Str {:example "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"})
   (s/optional-key :salt)
   (rjs/field s/Str {:example "gvXpBGp32DRIsPy1m1G3VlWHAF5nsi0auYnMIJQ0odZRKAGC"})
   (s/optional-key :prime) (rjs/field s/Str {:example 'prime4096})
   (s/optional-key :max) (rjs/field s/Int {:example 2048})
   })


(s/defschema Secret
  {(s/required-key :secret)
   (rjs/field s/Str {:example "La gatta sul tetto che scotta"})
   (s/optional-key :config) config-scheme
   })


(s/defschema Shares
  {(s/required-key :shares)
   (rjs/field
    [s/Str]
    {:example
     ["3NQFX9V46VNDB2K394ZMUM8MLZRWNCZQKXN5WT42R57L6KBD3Z7VRL5B3864MNX9U6725R6EVHG"
      "KNGF57RRP369H5DX4KKWUQ9KZ8W99TL2PR3G2WFGE7D3X6MBD736WV3LFXZ85M3V8H9D9ZZE5KSL"
      "XLVF45ELQ7MGSZQEW2GP4C234G4WPQTKWKMM9MEBL79RLVV2SV5LZ569KFDLDNRWX6F932X74W6HQ"
      "REWFRXX9W3EECNR5QEMXXUWNR7RXNZBQER43PK5S84E2ZLR7T4KZDRZV4B569QZP5NSRGQ4D83ESK"
      "79PFVMVX4RK8FDRQ9DQGVCR3MV3V7DH7KDM6GZQAG3RM7P26H5MQ35L3GSR4X8N8KQF84KRKR59T3"]})
   (s/optional-key :config) config-scheme})

(def app
  (api
    {:swagger
     {:ui "/"
      :spec "/swagger.json"
      :version "0.1.0"
      :data {:info {:title "FXC-webapi"
                    :description "FXC web API for simple secret sharing"}
             :tags [{:name "secrets", :description "secrets.dyne.org"}]}}}

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
enough to retrieve the original secret."
        (ok (let [conf (get-config secret)]
              {:shares (fxc/encode conf (:secret secret))
               :config conf})))


      (POST "/combine" []
        :return Secret
        :body [shares Shares]
        :summary "Combine shares into a secret"
        (ok (let [conf (get-config shares)]
              {:secret (fxc/decode conf (:shares shares))
               :config conf})))
    )))
