(set-env!
 :source-paths #{"src"}
 :resource-paths #{"html"}
 :dependencies '[[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.170"]
                 [adzerk/boot-cljs "1.7.228-1"]
                 [pandeiro/boot-http "0.7.3"]
                 [adzerk/boot-reload "0.4.8"]
                 [adzerk/boot-cljs-repl "0.3.0"]
                 [com.cemerick/piggieback "0.2.2-SNAPSHOT"]
                 [weasel "0.7.0"]
                 [org.clojure/tools.nrepl "0.2.12"]
                 [compojure "1.5.0"]
                 [cljsjs/three "0.0.76-0"]])

(require '[adzerk.boot-cljs :refer [cljs]]
         '[pandeiro.boot-http :refer [serve]]
         '[adzerk.boot-reload :refer [reload]]
         '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]])

(task-options!
 repl {:middleware '[cemerick.piggieback/wrap-cljs-repl]})

(deftask dev
  "Launch Immediate Feedback Development Environment"
  []
  (comp
   (serve)
   (watch)
   (reload)
   (cljs-repl)
   (cljs)
   (target :dir #{"target"})))
