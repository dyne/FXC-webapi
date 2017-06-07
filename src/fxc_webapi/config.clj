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

(ns fxc-webapi.config
  (:require [clojure.java.io :as io]
            [schema.core :as s]
            [ring.swagger.json-schema :as rjs]
            [cheshire.core :refer :all]))

(def config-default {;; FXC
                     :total (Integer. 5)
                     :quorum (Integer. 3)
                     :max 2048
                     :prime "prime4096"
                     :alphabet "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
                     :salt "gvXpBGp32DRIsPy1m1G3VlWHAF5nsi0auYnMIJQ0odZRKAGC"
                     ;; OpenPGP
                     :pgp-pub-keyring "~/.gnupg/pubring.gpg"
                     :pgp-sec-keyring "~/.gnupg/secring.gpg"
                     })

(defn- k [type key default]
  (rjs/field type {:example (get default key)}))

(def config-scheme
  {
   (s/optional-key :total)    (k s/Int :total    config-default)
   (s/optional-key :quorum)   (k s/Int :quorum   config-default)
   (s/optional-key :alphabet) (k s/Str :alphabet config-default)
   (s/optional-key :salt)     (k s/Str :salt     config-default)
   (s/optional-key :prime)    (k s/Str :prime    config-default)
   (s/optional-key :max)      (k s/Int :max      config-default)
   (s/optional-key :pgp-pub-keyring) (k s/Str :pgp-pub-keyring config-default)
   (s/optional-key :pgp-sec-keyring) (k s/Str :pgp-sec-keyring config-default)
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




(defn config-read
  "read configurations from standard locations, overriding defaults or
  system-wide with user specific paths."
  ([] (config-read config-default))
  ([default]
   (let [home (System/getenv "HOME")
         pwd  (System/getenv "PWD" )]
     (loop [[p & paths] ["/etc/fxc/config.json"
                         (str home "/.fxc/config.json")
                         (str pwd "/config.json")]
            res default]
       (let [res (merge res
                        (if (.exists (io/as-file p))
                          (conj {:config p} (parse-stream (io/reader p) true))))]
         (if (empty? paths) (conj {:config false} res)
             (recur paths res)))))))

(defn config-write
  "write configurations to file"
  [conf file]
  (generate-stream conf (io/writer file)
                   {:pretty true}))

