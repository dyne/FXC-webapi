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

(defproject fxc-webapi "0.1.0-SNAPSHOT"
  :description "FXC web API for simple secret sharing"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [metosin/compojure-api "2.0.0-alpha1"]
                 [ring/ring-core "1.6.1"]
                 [ring/ring-defaults "0.3.0"]
                 [markdown-clj "0.9.99"]
                 [org.clojars.dyne/fxc "0.5.0"]]
  :ring {:handler fxc-webapi.handler/app}
  :uberjar-name "server.jar"
  :profiles {:dev {:dependencies [[javax.servlet/javax.servlet-api "3.1.0"]
                                  [cheshire "5.7.1"]
                                  [ring/ring-mock "0.3.0"]
                                  [midje "1.8.3"]]
                   :plugins [[lein-ring "0.10.0"]
                             [lein-midje "3.2"]]}})
