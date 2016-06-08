# clojurescript-pong-3d

Silly experiment with ClojureScript, WebGL and Three.js

http://volrath.github.com/clojurescript-pong-3d/

Based on: http://buildnewgames.com/webgl-threejs/

## Things I've done and things I've played with ##

The main idea of this project is to play with Clojure(Script), its core
libraries, its ecosystem and functional programming in general. I tried to
integrate as much things as I could.

Here's a list of the things I deliberately played with that were absolutely new
to me, excluding Clojure(Script) itself:

* [boot](http://boot-clj.com/)
* [boot-cljs](https://github.com/adzerk-oss/boot-cljs) and
  [cljs compilation options](https://github.com/clojure/clojurescript/wiki/Compiler-Options)
* [externs](https://github.com/clojure/clojurescript/wiki/Compiler-Options#externs)
  and
  [foreign libraries](https://github.com/clojure/clojurescript/wiki/Compiler-Options#foreign-libs)
* [cljsjs](cljsjs.github.io)
* [Three.js](http://threejs.org/)
* [ParEdit](https://www.emacswiki.org/emacs/ParEdit),
  [clojure-mode](https://github.com/clojure-emacs/clojure-mode),
  [clj-refactor](https://github.com/clojure-emacs/clj-refactor.el)
* nREPL - [Cider](cider.readthedocs.io)
* [Compojure](https://github.com/weavejester/compojure), although it's not being
  used right now
* Google Closure's event handling
* [RUM](https://github.com/tonsky/rum)
* Atoms, Protocols, Records

## Things I'm planning do in the future ##

* Test and start TDD
* Websockets and Sente, I want to be able to play against friends.
* Play a bit more with WebGL/Three.js, Particularly play with the Camera's
  position
* More on boot-http / httpkit / Compojure

