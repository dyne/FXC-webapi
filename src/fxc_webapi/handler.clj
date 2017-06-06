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
  {:shares [s/Str]
   :config config-scheme})

(def app
  (api
    {:swagger
     {:ui "/"
      :spec "/swagger.json"
      :data {:info {:title "FXC-webapi"
                    :description "FXC web API for simple secret sharing"}
             :tags [{:name "secrets", :description "secrets.dyne.org"}]}}}

    (context "/api/v1" []
      :tags ["FXC1"]

      (POST "/share" []
        :return Shares
        :body [secret Secret]
        :summary "Split a secret into shares"
        (ok (let [inconf (:config secret)
                  conf   (merge config-default inconf)]
              {:shares (fxc/encode conf (:secret secret))
               :config conf})))


      (POST "/combine" []
        :return Shares
        :body [shares Shares]
        :summary "Combine shares into a secret"
        (ok shares))

      )
    )
  )
