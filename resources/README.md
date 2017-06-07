# FXC web API - Simple Secret Sharing


<a href="https://www.dyne.org"><img
src="https://secrets.dyne.org/static/img/swbydyne.png"
alt="software by Dyne.org"
title="software by Dyne.org" class="pull-right"></a>


## Social and decentralised management of secrets

FXC crypto library status: [![Build Status](https://travis-ci.org/dyne/FXC.svg?branch=master)](https://travis-ci.org/dyne/FXC)

FXC code coverage: [![Code Climate](https://codeclimate.com/github/dyne/FXC.png)](https://codeclimate.com/github/dyne/FXC)

Secrets can be used to split a secret text into shares to be
distributed to friends. When all friends agree, the shares can be
combined to retrieve the original secret text, for instance to give
consensual access to a lost pin, a password, a list of passwords, a
private document or a key to an encrypted volume.

Secret sharing can be useful in many different situations and this
tool is a simple and well documented free and open source
implementation available for anyone to use from this website, but also
independently on an offline PC.

## Usage

### Run the application locally

`lein ring server`

### Packaging and running as standalone jar

```
lein do clean, ring uberjar
java -jar target/server.jar
```

### Packaging as war

`lein ring uberwar`

## License

Copyright (C) 2017 Dyne.org foundation

Sourcecode designed, written and maintained by
Denis Roio <jaromil@dyne.org>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

