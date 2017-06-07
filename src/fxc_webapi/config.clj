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

(defn def [key] {:example (key config-default)})

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

