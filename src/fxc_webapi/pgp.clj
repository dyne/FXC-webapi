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

(ns fxc-webapi.pgp
  (:require
   [clojure.java.io :as io]
   [clj-pgp.core :as pgp]
   [clj-pgp.keyring :as kr]
   [clj-pgp.generate :as pgp-gen]
   [clj-pgp.message :as pgp-msg]
   [clj-pgp.signature :as pgp-sign]
   ))

(def keyring (atom nil))

(defn init [conf]
  {:pre [(contains? conf :pgp-pub-keyring)
         (contains? conf :pgp-sec-keyring)]}

  (let [pub (kr/load-public-keyring
             (io/file (:pgp-pub-keyring conf)))
        sec (kr/load-secret-keyring
             (io/file (:pgp-sec-keyring conf)))]
    ;; (swap! keyring {:pub pub
    ;;                 :sec sec})
    {:public-keys (vec (map pgp/hex-id (kr/list-public-keys pub)))
     :secret-keys (vec (map pgp/hex-id (kr/list-secret-keys sec)))}))
